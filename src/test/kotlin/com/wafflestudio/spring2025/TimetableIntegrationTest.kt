package com.wafflestudio.spring2025

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.course.extract.repository.CourseExtractRepository
import com.wafflestudio.spring2025.course.extract.repository.CourseTimeExtractRepository
import com.wafflestudio.spring2025.course.dto.course.SearchCourseResponse
import com.wafflestudio.spring2025.helper.DataGenerator
import com.wafflestudio.spring2025.timetable.dto.CreateTimetableRequest
import com.wafflestudio.spring2025.timetable.dto.UpdateTimetableRequest
import com.wafflestudio.spring2025.timetable.model.Semester
import com.wafflestudio.spring2025.timetable.repository.TimetableRepository
import org.springframework.http.MediaType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import com.wafflestudio.spring2025.timetablecourse.dto.CreateTimetableCourseRequest
import org.hamcrest.Matchers.hasItems
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TimetableIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
        private val dataGenerator: DataGenerator,
        private val courseExtractRepository: CourseExtractRepository,
        private val courseTimeExtractRepository: CourseTimeExtractRepository,
        private val jdbc: org.springframework.jdbc.core.JdbcTemplate,
        private val timetableRepository: TimetableRepository,
    ) {
        @Test
        fun `should create a timetable`() {
            // 시간표를 생성할 수 있다
            val (user, token) = dataGenerator.generateUser()
            val request =
                CreateTimetableRequest(
                    name = "2025 Spring Schedule",
                    year = 2025,
                    semester = Semester.SPRING,
                )

            // when & then
            mvc
                .perform(
                    post("/api/v1/timetables")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("2025 Spring Schedule"))
                .andExpect(jsonPath("$.semester").value("SPRING"))
                .andExpect(jsonPath("$.year").value(2025))
        }

        @Test
        fun `should not create a timetable with blank name`() {
            // given
            val (user, token) = dataGenerator.generateUser()
            val request =
                CreateTimetableRequest(
                    name = " ",
                    year = 2025,
                    semester = Semester.SPRING,
                )

            // when & then
            mvc
                .perform(
                    post("/api/v1/timetables")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)),
                ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should retrieve all own timetables`() {
            // 자신의 모든 시간표 목록을 조회할 수 있다
            val (user, token) = dataGenerator.generateUser()
            dataGenerator.generateTimetable(user = user, name = "T1")
            dataGenerator.generateTimetable(user = user, name = "T2")

            // when & then
            mvc
                .perform(
                    get("/api/v1/timetables")
                        .header("Authorization", "Bearer $token"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("T1"))
                .andExpect(jsonPath("$[1].name").value("T2"))
        }

        @Test
        fun `should retrieve timetable details`() {
            // 시간표 상세 정보를 조회할 수 있다

            val (user, token) = dataGenerator.generateUser()
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
                    .andExpect(jsonPath("$.name").value(timetable.name))
                    .andExpect(jsonPath("$.year").value(timetable.year))
                    .andExpect(jsonPath("$.semester").value(timetable.semester.name))
                    .andExpect(jsonPath("$.courses", hasSize<Any>(1)))
                    .andExpect(jsonPath("$.courses[0].id").value(course.id))
                    .andExpect(jsonPath("$.totalCredit").value(3))
        }

        @Test
        fun `should update timetable name`() {
            // 시간표 이름을 수정할 수 있다
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user, name = "Old Name")

            val request = UpdateTimetableRequest(name = "Updated Name")

            // when & then
            mvc
                .perform(
                    patch("/api/v1/timetables/${timetable.id!!}")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(timetable.id!!))
                .andExpect(jsonPath("$.name").value("Updated Name"))
        }

        @Test
        fun `should not update another user's timetable`() {
            // 다른 사람의 시간표는 수정할 수 없다
            val (user1, token1) = dataGenerator.generateUser()
            val (user2, token2) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user1, name = "User1 Timetable")

            val request = UpdateTimetableRequest(name = "Hacked")

            // when & then
            mvc
                .perform(
                    patch("/api/v1/timetables/${timetable.id!!}")
                        .header("Authorization", "Bearer $token2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `should delete a timetable`() {
            // 시간표를 삭제할 수 있다
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user, name = "To Delete")

            // when & then
            mvc
                .perform(
                    delete("/api/v1/timetables/${timetable.id!!}")
                        .header("Authorization", "Bearer $token"),
                ).andExpect(status().isNoContent)

            // verify it's deleted
            assert(timetableRepository.findById(timetable.id!!).isEmpty)
        }

        @Test
        fun `should not delete another user's timetable`() {
            // 다른 사람의 시간표는 삭제할 수 없다
            val (user1, token1) = dataGenerator.generateUser()
            val (user2, token2) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user1, name = "User1 Timetable")

            // when & then
            mvc
                .perform(
                    delete("/api/v1/timetables/${timetable.id!!}")
                        .header("Authorization", "Bearer $token2"),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `should search for courses`() {
            // 강의를 검색할 수 있다
            val (_, token) = dataGenerator.generateUser()
            val keyword = "검색 테스트"
            val c1 = dataGenerator.generateCourse(title = "검색 테스트", professor = "prof1")
            val c2 = dataGenerator.generateCourse(title = "검색 테스트:", professor = "prof2")
            val c3 = dataGenerator.generateCourse(title = "대학영어", professor = "prof3")
            val c4 = dataGenerator.generateCourse(title = "prof4", professor = "검색 테스트")

            val expected = 3 // keyword 포함 강의 수
            
            mvc.perform(
                get("/api/v1/courses")
                    .param("year", "2025")
                    .param("semester", "FALL")
                    .param("query", keyword)
                    .param("size", "10")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data", hasSize<Any>(expected)))
                .andExpect(jsonPath("$.data[*].id",hasItems(c1.id!!.toInt(), c2.id!!.toInt(), c4.id!!.toInt())))
                .andExpect(jsonPath("$.paging.hasNext").value(false))
        }

        @Test
        fun `should add a course to timetable`() {
            // 시간표에 강의를 추가할 수 있다

            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user)
            val course = dataGenerator.generateCourse()
        
            val request = CreateTimetableCourseRequest(
                courseId = course.id!!,
            )

            mvc.perform(
                post("/api/v1/timetables/${timetable.id!!}/courses")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.timetableId").value(timetable.id!!))
                .andExpect(jsonPath("$.course.id").value(course.id!!))
        }

        @Test
        fun `should return error when adding overlapping course to timetable`() {
            // 시간표에 강의 추가 시, 시간이 겹치면 에러를 반환한다
        
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user)
            // 월요일 10:00~11:00
            val course1 = dataGenerator.generateCourse(weekday = 1, startMin = 600, endMin = 660)
            // 월요일 10:30~11:30
            val course2 = dataGenerator.generateCourse(weekday = 1, startMin = 630, endMin = 690)

            val request1 = CreateTimetableCourseRequest(
                courseId = course1.id!!,
            )

            val request2 = CreateTimetableCourseRequest(
                courseId = course2.id!!,
            )

            mvc.perform(
                post("/api/v1/timetables/${timetable.id!!}/courses")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request1))
            ).andExpect(status().isOk)

            mvc.perform(
                post("/api/v1/timetables/${timetable.id!!}/courses")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request2))
            ).andExpect(status().isConflict)
        }

        @Test
        fun `should not add a course to another user's timetable`() {
            // 다른 사람의 시간표에는 강의를 추가할 수 없다

            val (owner, _) = dataGenerator.generateUser()
            val (other, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = owner)
            val course = dataGenerator.generateCourse()

            val request = CreateTimetableCourseRequest(
                courseId = course.id!!,
            )

            mvc.perform(
                post("/api/v1/timetables/${timetable.id!!}/courses")
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

            val addRequest = CreateTimetableCourseRequest(
                courseId = course.id!!,
            )

            // 강의 추가
            mvc.perform(
                post("/api/v1/timetables/${timetable.id!!}/courses")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(addRequest))
            ).andExpect(status().isOk)

            // 삭제 요청
            mvc.perform(
                delete("/api/v1/timetables/${timetable.id!!}/courses/${course.id!!}")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
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
                delete("/api/v1/timetables/${timetable.id!!}/courses/${course.id!!}")
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isForbidden)
        }

        @Test
        // @Disabled("곧 안내드리겠습니다")
        @BeforeAll
        fun `should fetch and save course information from SNU course registration site`() {
            // 서울대 수강신청 사이트에서 강의 정보를 가져와 저장할 수 있다
            val year = 2025
            val semCode = "U000200002U000300001"

            val requestBody = """{"year":$year,"semCode":"$semCode"}"""

            mvc.perform(
                post("/course/extract/sync").contentType(MediaType.APPLICATION_JSON).content(requestBody)
            ).andExpect(status().isOk).andExpect(jsonPath("$.fetchedBytes").exists())
                .andExpect(jsonPath("$.insertedCourses").exists())
                .andExpect(jsonPath("$.insertedCourseTimes").exists())
                .andReturn()

            val afterFirstCourses = courseExtractRepository.count()
            val afterFirstTimes = courseTimeExtractRepository.count()

            // check
            assert(afterFirstCourses > 0)
            assert(afterFirstTimes > 0)
        }

        @Test
        fun `check saved datas with small queries`() {
            // 실제 데이터 저장되었는지 확인해보는 쿼리
            val year = 2025
            val semCode = "U000200002U000300001"

            val requestBody = """{"year":$year,"semCode":"$semCode"}"""

            /*mvc.perform(
                post("/course/extract/sync").contentType(MediaType.APPLICATION_JSON).content(requestBody)
            ).andExpect(status().isOk)*/

            val courseCnt = jdbc.queryForObject(
                "select count(*) from `courses` where `year`=? and `semester`=?",
                Long::class.java, year, "FALL"
            )

            val timeCnt = jdbc.queryForObject(
                """
                select count(*)
                from `course_time` ct
                join `courses` c on ct.`course_id` = c.`id`
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
                from `courses` c
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
                join `courses` c on c.`id` = ct.`course_id`
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

            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user)

            val course1 = dataGenerator.generateCourse(credit = 2)
            val course2 = dataGenerator.generateCourse(credit = 3)
            val course3 = dataGenerator.generateCourse(credit = 1)

            dataGenerator.addCourseToTimetable(timetable, course1)
            dataGenerator.addCourseToTimetable(timetable, course2)
            dataGenerator.addCourseToTimetable(timetable, course3)

            val id1 = requireNotNull(course1.id) { "not null" }.toInt()
            val id2 = requireNotNull(course2.id) { "not null" }.toInt()
            val id3 = requireNotNull(course3.id) { "not null" }.toInt()

                mvc.perform(
                    get("/api/v1/timetables/{id}", timetable.id)
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.courses", hasSize<Any>(3)))
                    .andExpect(jsonPath("$.courses[*].id", hasItems(id1, id2, id3)))
                    .andExpect(jsonPath("$.totalCredit").value(6))
        }

        @Test
        fun `should paginate correctly when searching for courses`() {
            // 강의 검색 시, 페이지네이션이 올바르게 동작한다
            val (_, token) = dataGenerator.generateUser()
            val keyword = "pagination test course"
            val pageSize = 10

            repeat(25) {
                dataGenerator.generateCourse(title = "$keyword #$it", professor = "prof$it")
            }

            val page1 =
                mvc.perform(
                    get("/api/v1/courses")
                        .param("year", "2025")
                        .param("semester", "FALL")
                        .param("query", keyword)
                        .param("size", pageSize.toString())
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andReturn()
                    .response
                    .getContentAsString(Charsets.UTF_8)
                    .let {
                        mapper.readValue(it, SearchCourseResponse::class.java)
                    }

            Assertions.assertTrue(page1.paging.hasNext)
            Assertions.assertTrue(page1.paging.nextCursor != null)
            Assertions.assertEquals(pageSize, page1.data.size)

            val page2 =
                mvc.perform(
                    get("/api/v1/courses")
                        .param("year", "2025")
                        .param("semester", "FALL")
                        .param("query", keyword)
                        .param("size", pageSize.toString())
                        .param("cursor", page1.paging.nextCursor.toString())
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andReturn()
                    .response
                    .getContentAsString(Charsets.UTF_8)
                    .let {
                        mapper.readValue(it, SearchCourseResponse::class.java)
                    }

            Assertions.assertTrue(page2.paging.hasNext)
            Assertions.assertTrue(page2.paging.nextCursor != null)
            Assertions.assertEquals(pageSize, page2.data.size)

            val page3 =
                mvc.perform(
                    get("/api/v1/courses")
                        .param("year", "2025")
                        .param("semester", "FALL")
                        .param("query", keyword)
                        .param("size", pageSize.toString())
                        .param("cursor", page2.paging.nextCursor.toString())
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andReturn()
                    .response
                    .getContentAsString(Charsets.UTF_8)
                    .let {
                        mapper.readValue(it, SearchCourseResponse::class.java)
                    }

            Assertions.assertEquals(false, page3.paging.hasNext)
            Assertions.assertEquals(5, page3.data.size)
        }
    }
