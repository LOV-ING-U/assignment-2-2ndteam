package com.wafflestudio.spring2025.course.controller

import com.wafflestudio.spring2025.course.dto.course.SearchCourseRequest
import com.wafflestudio.spring2025.course.dto.course.SearchCourseResponse
import com.wafflestudio.spring2025.course.service.CourseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/courses")
class CourseController (
    private val courseService: CourseService
) {
    @GetMapping
    fun search(
        @RequestParam year: Int,
        @RequestParam semester: String,
        @RequestParam keyword: String?,
        @RequestParam limit: Int,
        @RequestParam nextId: Long?
    ): ResponseEntity<SearchCourseResponse> {
        val request = SearchCourseRequest(
            year = year,
            semester = semester,
            keyword = keyword,
            limit = limit,
            nextId = nextId
        )
        return ResponseEntity.ok(courseService.search(request))
    }
}