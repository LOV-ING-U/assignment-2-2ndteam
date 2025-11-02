package com.wafflestudio.spring2025.timetablecourse.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("timetable_course")
class TimetableCourse(
    @Id var id: Long? = null,
    var timetableId: Long,
    var courseId: Long,
)
