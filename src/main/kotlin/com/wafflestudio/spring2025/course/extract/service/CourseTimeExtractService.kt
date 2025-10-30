package com.wafflestudio.spring2025.course.extract.service

import com.wafflestudio.spring2025.course.extract.repository.CourseExtractRepository
import com.wafflestudio.spring2025.course.extract.repository.CourseTimeExtractRepository
import com.wafflestudio.spring2025.course.model.CourseTime
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.springframework.data.jdbc.core.convert.IdGeneratingEntityCallback
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import javax.sql.DataSource

@Service
class CourseTimeExtractService(
    private val courseExtractRepository: CourseExtractRepository,
    private val courseTimeExtractRepository: CourseTimeExtractRepository,
){
    private val keys = listOf("교과구분", "개설대학", "개설학과", "이수과정", "학년", "교과목번호",
        "강좌번호", "교과목명", "부제명", "학점", "강의", "실습", "수업교시", "수업형태", "강의실",
        "주담당교수", "정원", "강의언어")

    @Transactional
    fun importTimesFromXls(
        year: Int,
        semester: String,
        inputStream: InputStream
    ): Int {
        HSSFWorkbook(inputStream).use { hssfWorkbook ->
            val sheet = hssfWorkbook.getSheetAt(0)

            val header = sheet.getRow(2)
            val headerColNameAndIdxMap = header.associate{ it.toString() to it.columnIndex }

            // defensive programming
            keys.forEach { key ->
                require(headerColNameAndIdxMap.keys.any { key in it }) {
                    "there is no '$key' column in excel sheet.\n"
                }
            }

            val get = fun(row: Row, key: String): String {
                val idx = headerColNameAndIdxMap.entries.firstOrNull { it.key.contains(key) }?.value ?: return ""
                return row.getCell(idx)?.toString()?.trim() ?: ""
            }

            // extract time
            val collected = mutableListOf<Triple<Long, String, String>>()

            for(r in 3..sheet.lastRowNum){
                val row = sheet.getRow(r) ?: continue

                val courseNumber = get(row, "교과목번호")
                val classNumber = get(row, "강좌번호")
                if(courseNumber.isEmpty() || classNumber.isEmpty()) continue

                val course = courseExtractRepository.findByYearAndSemesterAndCourseNumberAndClassNumber(year, semester, courseNumber, classNumber) ?: continue
                val courseId = course.id ?: continue

                val timeString = get(row, "수업교시")
                val roomString = get(row, "강의실")

                collected += Triple(courseId, timeString, roomString)
            }

            var inserted = 0
            val cleared = mutableSetOf<Long>()

            for((courseId, timeString, roomString) in collected) {
                val slots: List<Slot> = transformData(timeString, roomString)
                if (slots.isEmpty()) continue

                if (cleared.add(courseId)) courseTimeExtractRepository.deleteByCourseId(courseId)

                for(s in slots){
                    courseTimeExtractRepository.save(
                        CourseTime(
                            id = null,
                            courseId = courseId,
                            weekday = s.weekday,
                            startMin = s.startMin,
                            endMin = s.endMin,
                            location = s.location.ifBlank { null }
                        )
                    )

                    inserted++
                }
            }

            return inserted
        }
    }
}

private data class Slot(
    val weekday: Int,
    val startMin: Int,
    val endMin: Int,
    val location: String
)

// helper function : 월11:00~ 및 301-105 -> dataSource
private fun transformData(
    timeStringRaw: String,
    roomStringRaw: String
): List<Slot> {
    val dayMap = mapOf('월' to 1, '화' to 2, '수' to 3, '목' to 4, '금' to 5, '토' to 6, '일' to 7)

    if(timeStringRaw.isBlank()) return emptyList()

    val roomString = roomStringRaw.replace("\n", "/").replace("(무선랜제공)", "").trim()
    val timeString = timeStringRaw.replace("\n", "/").replace("\\s+".toRegex(), "")

    val time = timeString.split("/").filter { it.isNotBlank() }
    val room = if(roomString.isBlank()) emptyList() else roomString.split("/")

    val location = { i: Int -> room.getOrNull(i) ?: if(room.size == 1) room[0] else "" }

    val regex = Regex("""^([월화수목금토일])\((\d{1,2}):(\d{2})~(\d{1,2}):(\d{2})\)$""")

    val result = mutableListOf<Slot>()

    time.forEachIndexed { i, part ->
        val m = regex.matchEntire(part) ?: return@forEachIndexed

        val day = m.groupValues[1].first()
        val startH = m.groupValues[2].toInt()
        val startMin = m.groupValues[3].toInt()
        val endH = m.groupValues[4].toInt()
        val endMin = m.groupValues[5].toInt()

        val weekday = dayMap[day] ?: return@forEachIndexed
        val startTime = startH * 60 + startMin
        val endTime = endH * 60 + endMin

        result += Slot(
            weekday = weekday,
            startMin = startTime,
            endMin = endTime,
            location = location(i)
        )
    }

    return result
}