package com.wafflestudio.spring2025.timetable.dto

import com.wafflestudio.spring2025.timetable.model.Semester

data class CreateTimetableRequest(
    val name: String,
    val year: Int,
    val semester: Semester,
)
