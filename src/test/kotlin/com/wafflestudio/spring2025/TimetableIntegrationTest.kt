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
        private val courseTimeExtractRepository: CourseTimeExtractRepository
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
            val semCode = "U000200002"

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
            assert(afterSecondTimes == afterFirstTimes)
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
