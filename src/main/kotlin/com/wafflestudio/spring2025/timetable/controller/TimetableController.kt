package com.wafflestudio.spring2025.timetable.controller

import com.wafflestudio.spring2025.timetable.dto.CreateTimetableRequest
import com.wafflestudio.spring2025.timetable.dto.CreateTimetableResponse
import com.wafflestudio.spring2025.timetable.service.TimetableService
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/timetables")
class TimetableController(
    private val timetableService: TimetableService,
) {
    @PostMapping
    fun create(
        @RequestBody createRequest: CreateTimetableRequest,
        @LoggedInUser user: User,
    ): ResponseEntity<CreateTimetableResponse> {
        val timetable =
            timetableService.create(
                name = createRequest.name,
                year = createRequest.year,
                semester = createRequest.semester,
                user = user,
            )
        return ResponseEntity.ok(timetable)
    }

}
