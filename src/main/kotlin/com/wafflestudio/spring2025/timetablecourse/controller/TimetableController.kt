package com.wafflestudio.spring2025.timetablecourse.controller

import com.wafflestudio.spring2025.timetablecourse.dto.CreateTimetableCourseRequest
import com.wafflestudio.spring2025.timetablecourse.dto.CreateTimetableCourseResponse
import com.wafflestudio.spring2025.timetablecourse.service.TimetableCourseService
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/timetable-courses")
// /api/vi/timetables/{timetableId}/courses << 이렇게 해도 되기는 하지만..?
class TimetableCourseController(
    private val timetableCourseService: TimetableCourseService,
) {
    @PostMapping
    fun create(
        @RequestBody createRequest: CreateTimetableCourseRequest,
        @LoggedInUser user: User,
    ): ResponseEntity<CreateTimetableCourseResponse> {
        val timetableCourse =
            timetableCourseService.create(
                timetableId = createRequest.timetableId,
                courseId = createRequest.courseId,
                user = user,
            )
        return ResponseEntity.ok(timetableCourse)
    }
}
