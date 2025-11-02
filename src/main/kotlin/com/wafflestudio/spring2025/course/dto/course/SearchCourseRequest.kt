package com.wafflestudio.spring2025.course.dto.course

data class SearchCourseRequest(
    val year: Int,
    val semester: String,
    val keyword: String? = null,
    val limit: Int = 20,
    val nextId: Long? = null,
)
