package com.wafflestudio.spring2025.timetablecourse.repository

import com.wafflestudio.spring2025.timetablecourse.model.TimetableCourse
import org.springframework.data.repository.ListCrudRepository

interface TimetableCourseRepository : ListCrudRepository<Timetable, Long> {
    fun existsByTimetableIdAndCourseId(timetableId: Long, courseId: Long): Boolean
}
