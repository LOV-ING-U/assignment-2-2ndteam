package com.wafflestudio.spring2025.course.extract.service

import com.wafflestudio.spring2025.course.extract.repository.CourseExtractRepository
import com.wafflestudio.spring2025.course.model.Course
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Row

@Service
class CourseExtractService(
    private val courseExtractRepository: CourseExtractRepository
){
    private val keys = listOf("교과구분", "개설대학", "개설학과", "이수과정", "학년", "교과목번호",
        "강좌번호", "교과목명", "부제명", "학점", "강의", "실습", "수업교시", "수업형태", "강의실",
        "주담당교수", "정원", "강의언어")

    @Transactional
    fun readInputXlsAndPutInDB(
        year: Int,
        semester: String,
        inputStream: InputStream
    ): Int {
        HSSFWorkbook(inputStream).use { hssfWorkbook ->
            val sheet: Sheet = hssfWorkbook.getSheetAt(0)
            val headerRow = sheet.getRow(2)
            val headerColNameAndIdxMap = headerRow.associate{ it.toString() to it.columnIndex }

            // defensive programming
            keys.forEach { key ->
                require(headerColNameAndIdxMap.keys.any { key in it }) {
                    "there is no '$key' column in excel sheet.\n"
                }
            }

            val get: (Row, String) -> String = { row, key ->
                val idx = headerColNameAndIdxMap.entries.firstOrNull { it.key.contains(key) }?.value
                row.getCell(idx!!)?.toString()!!
            }

            var inserted = 0
            for(n in 3..sheet.lastRowNum){
                val row = sheet.getRow(n) ?: continue

                val category = get(row, "교과구분")
                val college = get(row, "개설대학")
                val department = get(row, "개설학과")
                val procedure = get(row, "이수과정")
                val grade = get(row, "학년").toIntOrNull() ?: 0
                val courseNumber = get(row, "교과목번호")
                val classNumber = get(row, "강좌번호")
                val name = get(row, "교과목명")
                val subName = get(row, "부제명")
                val credit = get(row, "학점").toIntOrNull() ?: 0
                val professor = get(row, "주담당교수")
                val room = get(row, "강의실(동-호)(#연건, *평창)")

                courseExtractRepository.save(
                    Course(
                        id = null,
                        year = year,
                        semester = semester,
                        category = category,
                        college = college,
                        department = department,
                        procedure = procedure,
                        grade = grade,
                        courseNumber = courseNumber,
                        classNumber = classNumber,
                        name = name,
                        subName = subName,
                        credit = credit,
                        professor = professor,
                        room = room
                    )
                )

                inserted++
            }

            return inserted
        }
    }
}