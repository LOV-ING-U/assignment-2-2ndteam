package com.wafflestudio.spring2025.course.extract.dto

data class CourseSyncResultDto (
    val year: Int,
    val semester: String,
    val fetchedBytes: Int,
    val insertedCourses: Int,
    val insertedCourseTimes: Int
)