package com.wafflestudio.spring2025.course.repository

import com.wafflestudio.spring2025.course.model.CourseTime
import org.springframework.data.repository.CrudRepository

interface CourseTimeRepository : CrudRepository<CourseTime, Long> {
    fun findByCourseId(courseId: Long): List<CourseTime>

    fun findByCourseIdIn(courseIds: List<Long>): List<CourseTime>
}
