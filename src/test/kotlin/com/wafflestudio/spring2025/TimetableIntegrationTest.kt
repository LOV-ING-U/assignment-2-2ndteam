package com.wafflestudio.spring2025

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.course.extract.repository.CourseExtractRepository
import com.wafflestudio.spring2025.course.extract.repository.CourseTimeExtractRepository
import com.wafflestudio.spring2025.helper.DataGenerator
import com.wafflestudio.spring2025.timetable.dto.CreateTimetableRequest
import com.wafflestudio.spring2025.timetable.dto.UpdateTimetableRequest
import com.wafflestudio.spring2025.timetable.model.Semester
import com.wafflestudio.spring2025.timetable.repository.TimetableRepository
import org.junit.jupiter.api.Disabled
import org.springframework.http.MediaType as HttpMediaType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
<<<<<<< HEAD
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers
=======
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers
import com.wafflestudio.spring2025.course.extract.repository.CourseExtractRepository
import com.wafflestudio.spring2025.course.extract.repository.CourseTimeExtractRepository
import com.wafflestudio.spring2025.course.model.Course
>>>>>>> b31d2ab66b7d6f254d2584a8ffa74f69a2553db7
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
<<<<<<< HEAD
        private val courseExtractRepository: CourseExtractRepository,
        private val courseTimeExtractRepository: CourseTimeExtractRepository,
        private val jdbc: org.springframework.jdbc.core.JdbcTemplate
=======
        private val timetableRepository: TimetableRepository,
>>>>>>> b31d2ab66b7d6f254d2584a8ffa74f69a2553db7
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
            val year = "2025"
            val semester = "2"
            val keyword = "전자"

            // 키워드가 교수명에 포함되는 강의
            courseExtractRepository.save(
                Course(
                    id = null,
                    year = 2025,
                    semester = "2",
                    category = "전선",
                    college = "사범대학",
                    department = "사회교육과",
                    procedure = "학사",
                    grade = 4,
                    courseNumber = "M1855.001800",
                    classNumber = "001",
                    title = "문화와 사회",
                    subtitle = "",
                    credit = 3,
                    professor = "전자배",
                    room = "11-108",
                ),
            )

            // 키워드가 강의명에 포함되는 강의
            courseExtractRepository.save(
                Course(
                    id = null,
                    year = 2025,
                    semester = "2",
                    category = "전필",
                    college = "공과대학",
                    department = "전기·정보공학부",
                    procedure = "학사",
                    grade = 2,
                    courseNumber = "430.202B",
                    classNumber = "003",
                    title = "기초전자기학 및 연습",
                    subtitle = "",
                    credit = 4,
                    professor = "오정석",
                    room = "301-102(무선랜제공)/301-207(무선랜제공)/301-102(무선랜제공)",
                ),
            )

            // 키워드가 교수명과 강의명에 포함되지 않는 강의
            courseExtractRepository.save(
                Course(
                    id = null,
                    year = 2025,
                    semester = "2",
                    category = "전필",
                    college = "공과대학",
                    department = "전기·정보공학부",
                    procedure = "학사",
                    grade = 2,
                    courseNumber = "430.201A",
                    classNumber = "003",
                    title = "논리설계 및 실험",
                    subtitle = "",
                    credit = 4,
                    professor = "최우석",
                    room = "301-102(무선랜제공)/301-102(무선랜제공)/301-308(무선랜제공)",
                ),
            )

            // 연, 학기가 다른 강의
            courseExtractRepository.save(
                Course(
                    id = null,
                    year = 2024,
                    semester = "2",
                    category = "전필",
                    college = "공과대학",
                    department = "전기·정보공학부",
                    procedure = "학사",
                    grade = 2,
                    courseNumber = "430.202B",
                    classNumber = "003",
                    title = "기초전자기학및연습",
                    subtitle = "",
                    credit = 4,
                    professor = "오정석",
                    room = "301-102(무선랜제공)/301-207(무선랜제공)/301-102(무선랜제공)",
                ),
            )

            // 연, 학기가 다른 강의
            courseExtractRepository.save(
                Course(
                    id = null,
                    year = 2024,
                    semester = "2",
                    category = "전선",
                    college = "사범대학",
                    department = "사회교육과",
                    procedure = "학사",
                    grade = 4,
                    courseNumber = "M1855.001800",
                    classNumber = "001",
                    title = "문화와사회",
                    subtitle = "",
                    credit = 3,
                    professor = "전자배",
                    room = "11-108",
                ),
            )

            val mvcResult = mvc.perform(
                get("/api/v1/courses")
                    .param("year", year)
                    .param("semester", semester)
                    .param("query", keyword)
            ).andExpect(status().isOk)
                .andReturn()

            val response = mvcResult.response.getContentAsString()

            if (!response.contains("기초전자기학 및 연습")) {
                throw Exception("조건에 해당하는 강의가 검색되지 않았습니다.")
            }
            if (!response.contains("문화와 사회")) {
                throw Exception("조건에 해당하는 강의가 검색되지 않았습니다.")
            }

            val count = response.split("\"title\"").size - 1
            if (count != 2) {
                throw Exception("조건에 해당하지 않는 강의가 검색되었습니다.")
            }
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

            val year = "2025"
            val semester = "2"

            for (i in 1..25) {
                courseExtractRepository.save(
                    com.wafflestudio.spring2025.course.model.Course(
                        id = null,
                        year = 2025,
                        semester = "2",
                        category = "교양",
                        college = "학부대학",
                        department = "학부대학",
                        procedure = "학부",
                        grade = 1,
                        courseNumber = "F11.203",
                        classNumber = "$i",
                        title = "대학 글쓰기 $i",
                        subtitle = "",
                        credit = 2,
                        professor = "교수 $i",
                        room = "43-1-$i",
                    )
                )
            }

            courseExtractRepository.save(
                com.wafflestudio.spring2025.course.model.Course(
                    id = null,
                    year = 2024,
                    semester = "2",
                    category = "교양",
                    college = "학부대학",
                    department = "학부대학",
                    procedure = "학부",
                    grade = 1,
                    courseNumber = "F11.203",
                    classNumber = "001",
                    title = "대학 글쓰기 1",
                    subtitle = "",
                    credit = 2,
                    professor = "교수",
                    room = "43-1-101",
                )
            )

            courseExtractRepository.save(
                Course(
                    id = null,
                    year = 2025,
                    semester = "2",
                    category = "전필",
                    college = "공과대학",
                    department = "전기·정보공학부",
                    procedure = "학사",
                    grade = 2,
                    courseNumber = "430.201A",
                    classNumber = "003",
                    title = "논리설계 및 실험",
                    subtitle = "",
                    credit = 4,
                    professor = "최우석",
                    room = "301-102(무선랜제공)/301-102(무선랜제공)/301-308(무선랜제공)",
                ),
            )

            val res1 = mvc.perform(
                get("/api/v1/courses")
                    .param("year", year)
                    .param("semester", semester)
                    .param("query", "대학 글쓰기")
                    .param("size", "10")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.length()").value(10))
                .andExpect(jsonPath("$.paging.hasNext").value(true))
                .andExpect(jsonPath("$.paging.nextCursor").isNotEmpty)
                .andReturn()

            val next1 = com.jayway.jsonpath.JsonPath.read<String>(
                res1.response.getContentAsString(), "$.paging.nextCursor"
            )

            val res2 = mvc.perform(
                get("/api/v1/courses")
                    .param("year", year)
                    .param("semester", semester)
                    .param("query", "대학 글쓰기")
                    .param("size", "10")
                    .param("cursor", next1)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.length()").value(10))
                .andExpect(jsonPath("$.paging.hasNext").value(true))
                .andExpect(jsonPath("$.paging.nextCursor").isNotEmpty)
                .andReturn()

            val next2 = com.jayway.jsonpath.JsonPath.read<String>(
                res2.response.getContentAsString(), "$.paging.nextCursor"
            )

            mvc.perform(
                get("/api/v1/courses")
                    .param("year", year)
                    .param("semester", semester)
                    .param("query", "대학 글쓰기")
                    .param("size", "10")
                    .param("cursor", next2)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.length()").value(5))
                .andExpect(jsonPath("$.paging.hasNext").value(false))
                .andExpect(jsonPath("$.paging.nextCursor").doesNotExist())
                .andReturn()
        }
    }
