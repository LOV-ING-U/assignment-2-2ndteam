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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers
import com.wafflestudio.spring2025.course.extract.repository.CourseExtractRepository
import com.wafflestudio.spring2025.course.extract.repository.CourseTimeExtractRepository
import com.wafflestudio.spring2025.course.model.Course
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

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
        private val courseExtractRepository: CourseExtractRepository,
        private val courseTimeExtractRepository: CourseTimeExtractRepository,
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
        @Disabled("곧 안내드리겠습니다")
        fun `should fetch and save course information from SNU course registration site`() {
            // 서울대 수강신청 사이트에서 강의 정보를 가져와 저장할 수 있다
        }

        @Test
        fun `should return correct course list and total credits when retrieving timetable details`() {
            // 시간표 상세 조회 시, 강의 정보 목록과 총 학점이 올바르게 반환된다
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
