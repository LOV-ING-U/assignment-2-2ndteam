package com.wafflestudio.spring2025.course.dto.course

data class SearchCourseResponse(
    val items: List<CourseDto>,
    val nextId: Long?
)