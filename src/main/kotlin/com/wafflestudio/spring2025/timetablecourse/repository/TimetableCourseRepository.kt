package com.wafflestudio.spring2025.timetablecourse.repository

import com.wafflestudio.spring2025.timetablecourse.model.TimetableCourse
import org.springframework.data.repository.ListCrudRepository

interface TimetableCourseRepository : ListCrudRepository<TimetableCourse, Long> {
    fun existsByTimetableIdAndCourseId(timetableId: Long, courseId: Long): Boolean
    fun findByTimetableId(timetableId: Long): List<TimetableCourse>
    fun deleteByTimetableIdAndCourseId(timetableId: Long, courseId: Long)
}
