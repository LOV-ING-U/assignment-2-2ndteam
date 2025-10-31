package com.wafflestudio.spring2025.timetable.dto.core

// @TODO

import com.wafflestudio.spring2025.timetable.model.Timetable

data class TimetableDto(
    val id: Long,
    val name: String,
    val year: Int,
    val semester: String,
) {
    constructor (timetable: Timetable) : this(
        id = timetable.id!!,
        name = timetable.name,
        year = timetable.year,
        semester = timetable.semester.name,
    )
}
