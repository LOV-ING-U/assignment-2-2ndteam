package com.wafflestudio.spring2025.timetablecourse.dto.core

import com.wafflestudio.spring2025.timetablecourse.model.TimetableCourse
import com.wafflestudio.spring2025.course.model.Course
import com.wafflestudio.spring2025.course.dto.core.CourseDto
import com.wafflestudio.spring2025.timetable.model.Timetable

data class TimetableDetailDto(
    val id: Long?,
    val name: String,
    val year: Int,
    val semester: String,
    val totalCredit: Int,
    val courses: List<CourseDto>
) {
    constructor (timetable: Timetable, totalCredit: Int, courses: List<CourseDto>) : this(
        id = timetable.id,
        name = timetable.name,
        year = timetable.year,
        semester = timetable.semester,
        timetableId = timetableCourse.timetableId,
        totalCredit = totalCredit
        courses = courses
    )
}
