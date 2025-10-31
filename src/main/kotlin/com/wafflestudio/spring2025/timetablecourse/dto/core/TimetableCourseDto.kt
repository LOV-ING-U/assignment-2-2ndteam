package com.wafflestudio.spring2025.timetablecourse.dto.core

import com.wafflestudio.spring2025.timetablecourse.model.TimetableCourse
import com.wafflestudio.spring2025.course.model.Course
import com.wafflestudio.spring2025.course.dto.core.CourseDto
import com.wafflestudio.spring2025.timetable.model.Timetable

data class TimetableCourseDto(
    val id: Long?,
    val timetableId: Long,
    val course: CourseDto,
) {
    constructor (timetableCourse: TimetableCourse, course: Course) : this(
        id = timetableCourse.id,
        timetableId = timetableCourse.timetableId,
        course = CourseDto(course)
    )
}
