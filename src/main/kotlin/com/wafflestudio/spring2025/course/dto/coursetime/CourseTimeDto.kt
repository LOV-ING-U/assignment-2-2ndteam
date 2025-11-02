package com.wafflestudio.spring2025.course.dto.coursetime

data class CourseTimeDto(
    val weekday: Int,
    val startMin: Int,
    val endMin: Int,
    val location: String?,
)
