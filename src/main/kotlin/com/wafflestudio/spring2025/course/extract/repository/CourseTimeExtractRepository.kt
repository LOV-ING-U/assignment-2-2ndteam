package com.wafflestudio.spring2025.course.extract.repository

import com.wafflestudio.spring2025.course.model.CourseTime
import org.springframework.data.repository.CrudRepository

interface CourseTimeExtractRepository : CrudRepository<CourseTime, Long> {
    fun deleteByCourseId(courseId: Long): Long
}
