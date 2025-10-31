package com.wafflestudio.spring2025.timetablecourse.dto.core

import com.wafflestudio.spring2025.course.dto.course.CourseDto
import com.wafflestudio.spring2025.timetable.model.Semester
import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.timetablecourse.model.TimetableCourse

data class TimetableDetailDto(
    val id: Long?,
    val name: String,
    val year: Int,
    val semester: Semester,
    val totalCredit: Int,
    val courses: List<CourseDto>
) {
    constructor (timetable: Timetable, totalCredit: Int, courses: List<CourseDto>) : this(
        id = timetable.id,
        name = timetable.name,
        year = timetable.year,
        semester = timetable.semester,
        totalCredit = totalCredit,
        courses = courses
    )
}
