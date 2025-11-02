package com.wafflestudio.spring2025.timetable.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("timetables")
class Timetable(
    @Id var id: Long? = null,
    var name: String,
    var year: Int,
    var semester: Semester,
    @Column("user_id")
    var userId: Long,
)

enum class Semester {
    SPRING,
    SUMMER,
    FALL,
    WINTER,
}
