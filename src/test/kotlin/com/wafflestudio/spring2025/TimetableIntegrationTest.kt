package com.wafflestudio.spring2025

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.course.extract.repository.CourseExtractRepository
import com.wafflestudio.spring2025.course.extract.repository.CourseTimeExtractRepository
import com.wafflestudio.spring2025.helper.DataGenerator
import org.junit.jupiter.api.Disabled
import org.springframework.http.MediaType as HttpMediaType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
class TimetableIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
        private val dataGenerator: DataGenerator,
        private val courseExtractRepository: CourseExtractRepository,
        private val courseTimeExtractRepository: CourseTimeExtractRepository,
        private val jdbc: org.springframework.jdbc.core.JdbcTemplate
    ) {
        @Test
        fun `should create a timetable`() {
            // 시간표를 생성할 수 있다
        }

        @Test
        fun `should retrieve all own timetables`() {
            // 자신의 모든 시간표 목록을 조회할 수 있다
        }

        @Test
        fun `should retrieve timetable details`() {
            // 시간표 상세 정보를 조회할 수 있다
        }

        @Test
        fun `should update timetable name`() {
            // 시간표 이름을 수정할 수 있다
        }

        @Test
        fun `should not update another user's timetable`() {
            // 다른 사람의 시간표는 수정할 수 없다
        }

        @Test
        fun `should delete a timetable`() {
            // 시간표를 삭제할 수 있다
        }

        @Test
        fun `should not delete another user's timetable`() {
            // 다른 사람의 시간표는 삭제할 수 없다
        }

        @Test
        fun `should search for courses`() {
            // 강의를 검색할 수 있다
        }

        @Test
        fun `should add a course to timetable`() {
            // 시간표에 강의를 추가할 수 있다
        }

        @Test
        fun `should return error when adding overlapping course to timetable`() {
            // 시간표에 강의 추가 시, 시간이 겹치면 에러를 반환한다
        }

        @Test
        fun `should not add a course to another user's timetable`() {
            // 다른 사람의 시간표에는 강의를 추가할 수 없다
        }

        @Test
        fun `should remove a course from timetable`() {
            // 시간표에서 강의를 삭제할 수 있다
        }

        @Test
        fun `should not remove a course from another user's timetable`() {
            // 다른 사람의 시간표에서는 강의를 삭제할 수 없다
        }

        @Test
        // @Disabled("곧 안내드리겠습니다")
        fun `should fetch and save course information from SNU course registration site`() {
            // 서울대 수강신청 사이트에서 강의 정보를 가져와 저장할 수 있다
            val year = 2025
            val semCode = "U000200002U000300001"

            val beforeCourses = courseExtractRepository.count()
            val beforeTimes = courseTimeExtractRepository.count()

            val requestBody = """{"year":$year,"semCode":"$semCode"}"""

            mvc.perform(
                post("/course/extract/sync").contentType(HttpMediaType.APPLICATION_JSON).content(requestBody)
            ).andExpect(status().isOk).andExpect(jsonPath("$.fetchedBytes").exists())
                .andExpect(jsonPath("$.insertedCourses").exists())
                .andExpect(jsonPath("$.insertedCourseTimes").exists())
                .andReturn()

            val afterFirstCourses = courseExtractRepository.count()
            val afterFirstTimes = courseTimeExtractRepository.count()

            // check
            assert(afterFirstCourses > beforeCourses)
            assert(afterFirstTimes > beforeTimes)

            /*
            // again, and check if DB is not changed
            mvc.perform(
                post("/course/extract/sync").contentType(HttpMediaType.APPLICATION_JSON).content(requestBody)
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.insertedCourses").isNumber)
                .andExpect(jsonPath("$.insertedCourseTimes").isNumber)

            val afterSecondCourses = courseExtractRepository.count()
            val afterSecondTimes = courseTimeExtractRepository.count()

            // check
            assert(afterSecondCourses == afterFirstCourses)
            assert(afterSecondTimes == afterFirstTimes)*/
        }

        @Test
        fun `check saved datas with small queries`() {
            // 실제 데이터 저장되었는지 확인해보는 쿼리
            val year = 2025
            val semCode = "U000200002U000300001"

            val requestBody = """{"year":$year,"semCode":"$semCode"}"""

            mvc.perform(
                post("/course/extract/sync").contentType(HttpMediaType.APPLICATION_JSON).content(requestBody)
            ).andExpect(status().isOk)

            val courseCnt = jdbc.queryForObject(
                "select count(*) from `course` where `year`=? and `semester`=?",
                Long::class.java, year, "FALL"
            )

            val timeCnt = jdbc.queryForObject(
                """
                select count(*)
                from `course_time` ct
                join `course` c on ct.`course_id` = c.`id`
                where c.`year`=? and c.`semester`=?
                """.trimIndent(),
                Long::class.java, year, "FALL"
            )

            println("Course count = $courseCnt, Course_time count = $timeCnt\n")

            // select for class number
            // 1. see all other classes which code is fixed with 'courseNumberCode'
            // fix this code (F25.101 = 초급중국어 1)
            val courseNumberCode = "F25.101"
            val seeAllOtherClasses = jdbc.queryForList(
                """
                select c.`courseNumber`, c.`classNumber`
                from `course` c
                where c.`courseNumber`=?           
                """.trimIndent(),
                courseNumberCode
            )
            println("Number of same classNumber = ${seeAllOtherClasses.size}\n")
            seeAllOtherClasses.forEachIndexed { i, row ->
                println(
                    "courseNumber = ${row["courseNumber"]}, " +
                    "classNumber = ${row["classNumber"]}\n"
                )
            }

            // 2. see all classes through number code
            val select = jdbc.queryForList(
                """
                select ct.`course_id`, ct.`weekday`, ct.`start_min`, ct.`end_min`, ct.`location`
                from `course_time` ct
                join `course` c on c.`id` = ct.`course_id`
                where c.`courseNumber`=?
                """.trimIndent(),
                courseNumberCode
            )

            println("Number of selected course = ${select.size}\n")
            select.forEachIndexed { i, row ->
                println(
                    "#$i -> course_id = ${row["course_id"]}, " +
                    "weekday = ${row["weekday"]}, " +
                    "start_min = ${row["start_min"]}, " +
                    "end_min = ${row["end_min"]}, " +
                    "location = ${row["location"]}\n"
                )
            }
        }

        @Test
        fun `should return correct course list and total credits when retrieving timetable details`() {
            // 시간표 상세 조회 시, 강의 정보 목록과 총 학점이 올바르게 반환된다
        }

        @Test
        fun `should paginate correctly when searching for courses`() {
            // 강의 검색 시, 페이지네이션이 올바르게 동작한다
        }
    }
