package com.wafflestudio.spring2025.course.dto.course

import com.wafflestudio.spring2025.course.dto.coursetime.CourseTimeDto
import com.wafflestudio.spring2025.course.model.Course

data class CourseDto(
    val id: Long,
    val year: Int,
    val semester: String,
    val courseNumber: String,
    val classNumber: String,
    val title: String,
    val subtitle: String?,
    val credit: Int?,
    val professor: String?,
    val room: String?,
    val category: String?,
    val college: String?,
    val department: String?,
    val grade: Int?,
    val procedure: String?,
    val times: List<CourseTimeDto> = emptyList(),
) {
    constructor(c: Course, times: List<CourseTimeDto>) : this(
        id = c.id!!,
        year = c.year,
        semester = c.semester,
        courseNumber = c.courseNumber,
        classNumber = c.classNumber,
        title = c.title,
        subtitle = c.subtitle,
        credit = c.credit,
        professor = c.professor,
        room = c.room,
        category = c.category,
        college = c.college,
        department = c.department,
        grade = c.grade,
        procedure = c.procedure,
        times = times,
    )
}
