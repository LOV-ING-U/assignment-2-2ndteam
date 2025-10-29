package com.wafflestudio.spring2025.timetable.dto.core

// @TODO

import com.wafflestudio.spring2025.board.model.Board

data class TimetableDto(
    val id: Long,
    val name: String,
) {
    constructor (board: Board) : this(
        id = board.id!!,
        name = board.name,
    )
}
