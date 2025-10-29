package com.wafflestudio.spring2025.timetable.dto.core

import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User

data class TimetableDto(
    val id: Long?,
    val name: String,
    val user: UserDto,
    val year: Int,
    val semester: String,
    val createdAt: Long,
    val updatedAt: Long,
) {
    constructor (timetable: Timetable, user: User) : this(
        id = timetable.id,
        name = timetable.name,
        user = UserDto(user),
        year = timetable.year,
        semester = timetable.semester,
        createdAt = timetable.createdAt!!.toEpochMilli(),
        updatedAt = timetable.updatedAt!!.toEpochMilli(),
    )

    // 아마, Comment model처럼 다른 생성자도 추가해야 할 수도....?
}
