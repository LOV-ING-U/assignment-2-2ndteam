package com.wafflestudio.spring2025.timetablecourse.dto

data class DeleteTimetableCourseRequest(
    val timetableId: Long,
    val courseId: Long,
)
