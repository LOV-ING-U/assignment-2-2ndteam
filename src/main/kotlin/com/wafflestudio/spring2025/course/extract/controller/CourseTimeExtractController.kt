package com.wafflestudio.spring2025.course.extract.controller

import com.wafflestudio.spring2025.course.extract.dto.CourseTimeExtractDto
import com.wafflestudio.spring2025.course.extract.service.CourseTimeExtractService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/course/extract")
class CourseTimeExtractController (
    private val courseTimeExtractService: CourseTimeExtractService
){
    @PostMapping("/times")
    fun putXlsTimeInDB(
        @RequestParam year: Int,
        @RequestParam semester: String,
        @RequestPart file: MultipartFile
    ): ResponseEntity<CourseTimeExtractDto> {
        val inserted = courseTimeExtractService....;
        return ResponseEntity.ok(CourseTimeExtractDto(inserted))
    }
}