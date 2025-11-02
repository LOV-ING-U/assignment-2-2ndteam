package com.wafflestudio.spring2025.timetablecourse.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("timetable_course")
data class CreateTimetableCourseRequest(
    @Id val id: Long? = null,
    @Column("course_id") val courseId: Long,
)
