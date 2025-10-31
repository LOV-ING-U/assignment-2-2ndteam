package com.wafflestudio.spring2025.course.repository

import com.wafflestudio.spring2025.course.model.CourseTime
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface CourseTimeRepository : CrudRepository<CourseTime, Long> {

    @Query("SELECT * FROM course_time WHERE course_id IN (:ids)")
    fun findByCourseIds(@Param("ids") ids: List<Long>): List<CourseTime>
}