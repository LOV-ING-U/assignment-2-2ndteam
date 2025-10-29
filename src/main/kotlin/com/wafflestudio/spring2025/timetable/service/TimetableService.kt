package com.wafflestudio.spring2025.timetable.service

// @TODO

import com.wafflestudio.spring2025.board.BoardNameBlankException
import com.wafflestudio.spring2025.board.BoardNameConflictException
import com.wafflestudio.spring2025.board.dto.core.BoardDto
import com.wafflestudio.spring2025.board.model.Board
import com.wafflestudio.spring2025.board.repository.BoardRepository
import org.springframework.stereotype.Service

@Service
class TimetableService(
    private val boardRepository: BoardRepository,
) {
    fun create(name: String): BoardDto {
        if (name.isBlank()) {
            throw BoardNameBlankException()
        }
        if (boardRepository.existsByName(name)) {
            throw BoardNameConflictException()
        }
        val board =
            boardRepository.save(
                Board(
                    name = name,
                ),
            )
        return BoardDto(board)
    }

    fun list(): List<BoardDto> = boardRepository.findAll().map { BoardDto(it) }
}
