package com.wafflestudio.spring2025.course.dto.course

import com.wafflestudio.spring2025.course.dto.coursetime.CourseTimeDto

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
    val times: List<CourseTimeDto> = emptyList()
)