package com.wafflestudio.spring2025.course.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("course_time")
data class CourseTime(
    @Id var id: Long? = null,
    @Column("course_id") var courseId: Long,
    @Column("weekday") var weekday: Int,
    @Column("start_min") var startMin: Int,
    @Column("end_min") var endMin: Int,
    @Column("location") var location: String?,
)
