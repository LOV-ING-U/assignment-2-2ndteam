package com.wafflestudio.spring2025.course.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("course")
data class Course(
    @Id var id: Long? = null,
    var year: Int, // 개설년도
    var semester: String, // 개설학기

    var category: String, // 교과구분
    var college: String, // 개설대학
    var department: String, // 개설학과
    var procedure: String, // 이수과정
    var grade: Int, // 학년
    var courseNumber: String, // 교과목번호
    var classNumber: String, // 강좌번호
    var name: String, // 교과목명
    var subName: String, // 부제
    var credit: Int, // 학점
    var professor: String, // 주담당교수
    var room: String // 강의실(동-호)(#연건, *평창)
)