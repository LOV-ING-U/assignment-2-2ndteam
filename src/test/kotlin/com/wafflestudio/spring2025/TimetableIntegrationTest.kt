package com.wafflestudio.spring2025

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.helper.DataGenerator
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
class TimetableIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
        private val dataGenerator: DataGenerator,
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

            val (user, token) = dataGenerator.generateUser()
            // @TODO: 이런 식의 generate가 가능하다고 가정
            val timetable = dataGenerator.generateTimetable(user = user)
            val course = dataGenerator.generateCourse(credit = 3) 
            dataGenerator.addCourseToTimetable(timetable, course)

            val responseString =
                mvc.perform(
                    get("/api/v1/timetables/{id}", timetable.id)
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andReturn()
                    .response
                    .getContentAsString(Charsets.UTF_8)

            val response = mapper.readValue(responseString, TimetableDetailDto::class.java)

            assertEquals(timetable.name, response.name)
            assertEquals(timetable.year, response.year)
            assertEquals(timetable.semester, response.semester)
            assertEquals(1, response.courses.size)
            assertEquals(course.id, response.courses.first().id)
            assertEquals(3, response.totalCredit)
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

            val (user, token) = dataGenerator.generateUser()
            // @TODO: 일단 dataGenerator에 있다고 가정
            val timetable = dataGenerator.generateTimetable(user = user)
            val course = dataGenerator.generateCourse()
        
            val request = mapOf(
                "timetableId" to timetable.id,
                "courseId" to course.id
            )

            mvc.perform(
                post("/api/v1/timetable-courses")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.timetableId").value(timetable.id!!))
                .andExpect(jsonPath("$.course.id").value(course.id!!))
            }
        } 

        @Test
        fun `should return error when adding overlapping course to timetable`() {
            // 시간표에 강의 추가 시, 시간이 겹치면 에러를 반환한다
        
            val (user, token) = dataGenerator.generateUser()
            // @TODO: 이런 식의 generate가 가능하다고 가정
            val timetable = dataGenerator.generateTimetable(user = user)
            // 월요일 10:00~11:00
            val course1 = dataGenerator.generateCourse(weekday = 1, startMin = 600, endMin = 660)
            // 월요일 10:30~11:30
            val course2 = dataGenerator.generateCourse(weekday = 1, startMin = 630, endMin = 690)

            mvc.perform(
                post("/api/v1/timetable-courses")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(mapOf("timetableId" to timetable.id, "courseId" to course1.id)))
            ).andExpect(status().isOk)

            mvc.perform(
                post("/api/v1/timetable-courses")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(mapOf("timetableId" to timetable.id, "courseId" to course2.id)))
            ).andExpect(status().isConflict)
        }

        @Test
        fun `should not add a course to another user's timetable`() {
            // 다른 사람의 시간표에는 강의를 추가할 수 없다

            val (owner, _) = dataGenerator.generateUser()
            val (other, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = owner)
            val course = dataGenerator.generateCourse()

            val request = mapOf("timetableId" to timetable.id, "courseId" to course.id)

            mvc.perform(
                post("/api/v1/timetable-courses")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `should remove a course from timetable`() {
            // 시간표에서 강의를 삭제할 수 있다

            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user)
            val course = dataGenerator.generateCourse()

            // 강의 추가
            mvc.perform(
                post("/api/v1/timetable-courses")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(mapOf("timetableId" to timetable.id, "courseId" to course.id)))
            ).andExpect(status().isOk)

            // 삭제 요청
            mvc.perform(
                delete("/api/v1/timetable-courses")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(mapOf("timetableId" to timetable.id, "courseId" to course.id)))
            ).andExpect(status().isNoContent)
        }

        @Test
        fun `should not remove a course from another user's timetable`() {
            // 다른 사람의 시간표에서는 강의를 삭제할 수 없다

            val (owner, _) = dataGenerator.generateUser()
            val (other, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = owner)
            val course = dataGenerator.generateCourse()
            dataGenerator.addCourseToTimetable(timetable, course)

            mvc.perform(
                delete("/api/v1/timetable-courses")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(mapOf("timetableId" to timetable.id, "courseId" to course.id)))
            ).andExpect(status().isForbidden)
        }

        @Test
        @Disabled("곧 안내드리겠습니다")
        fun `should fetch and save course information from SNU course registration site`() {
            // 서울대 수강신청 사이트에서 강의 정보를 가져와 저장할 수 있다
        }

        @Test
        fun `should return correct course list and total credits when retrieving timetable details`() {
            // 시간표 상세 조회 시, 강의 정보 목록과 총 학점이 올바르게 반환된다

            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user)

            val course1 = dataGenerator.generateCourse(credit = 2)
            val course2 = dataGenerator.generateCourse(credit = 3)
            val course3 = dataGenerator.generateCourse(credit = 1)

            dataGenerator.addCourseToTimetable(timetable, course1)
            dataGenerator.addCourseToTimetable(timetable, course2)
            dataGenerator.addCourseToTimetable(timetable, course3)

            val responseString =
                mvc.perform(
                    get("/api/v1/timetables/{id}", timetable.id)
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andReturn()
                    .response
                    .getContentAsString(Charsets.UTF_8)

            val response = mapper.readValue(responseString, TimetableDetailDto::class.java)

            assertEquals(3, response.courses.size)
            assertTrue(response.courses.any { it.id == course1.id })
            assertTrue(response.courses.any { it.id == course2.id })
            assertTrue(response.courses.any { it.id == course3.id })
            assertEquals(6, response.totalCredit)
        }

        @Test
        fun `should paginate correctly when searching for courses`() {
            // 강의 검색 시, 페이지네이션이 올바르게 동작한다
        }
    }
