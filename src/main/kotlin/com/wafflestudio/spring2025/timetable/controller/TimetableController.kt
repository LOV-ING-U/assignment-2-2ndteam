package com.wafflestudio.spring2025.timetable.controller

// @TODO

import com.wafflestudio.spring2025.board.dto.CreateBoardRequest
import com.wafflestudio.spring2025.board.dto.CreateBoardResponse
import com.wafflestudio.spring2025.board.dto.ListBoardResponse
import com.wafflestudio.spring2025.board.service.BoardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/boards")
class TimetableController(
    private val boardService: BoardService,
) {
    @PostMapping
    fun create(
        @RequestBody createRequest: CreateBoardRequest,
    ): ResponseEntity<CreateBoardResponse> {
        val board = boardService.create(createRequest.name)
        return ResponseEntity.ok(board)
    }

    @GetMapping
    fun list(): ResponseEntity<ListBoardResponse> {
        val boards = boardService.list()
        return ResponseEntity.ok(boards)
    }
}
