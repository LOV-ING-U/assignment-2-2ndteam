package com.wafflestudio.spring2025.course.extract.repository

import com.wafflestudio.spring2025.course.model.Course
import org.springframework.data.repository.CrudRepository

interface CourseExtractRepository : CrudRepository<Course, Long>{
    fun findByYearAndSemesterAndCourseNumberAndClassNumber(
        year: Int,
        semester: String,
        courseNumber: String,
        classNumber: String
    ): Course?
}