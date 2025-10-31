package com.wafflestudio.spring2025.timetable.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("timetables")
class Timetable(
    @Id var id: Long? = null,
    var userId: Long,
    var name: String,
    var year: Int,
    var semester: Semester,
    @Column("user_id")
    var userId: Long,
    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
)

enum class Semester {
    SPRING,
    SUMMER,
    FALL,
    WINTER,
}
