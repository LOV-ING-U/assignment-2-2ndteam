package com.wafflestudio.spring2025.course.dto.course

data class SearchCourseResponse(
    val data: List<CourseDto>,
    val paging: Paging,
)

data class Paging(
    val hasNext: Boolean,
    val nextCursor: Long?,
)
