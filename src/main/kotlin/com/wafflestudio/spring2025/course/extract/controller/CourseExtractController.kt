package com.wafflestudio.spring2025.course.extract.controller

import com.wafflestudio.spring2025.course.extract.dto.CourseExtractDto
import com.wafflestudio.spring2025.course.extract.dto.CourseSyncRequestDto
import com.wafflestudio.spring2025.course.extract.dto.CourseSyncResultDto
import com.wafflestudio.spring2025.course.extract.service.CourseExtractService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/course/extract")
class CourseExtractController(
    private val courseExtractService: CourseExtractService,
) {
    @PostMapping("/put")
    fun putXlsInDB(
        @RequestParam year: Int,
        @RequestParam semester: String,
        @RequestPart file: MultipartFile,
    ): ResponseEntity<CourseExtractDto> {
        val inserted = courseExtractService.readInputXlsAndPutInDB(year, semester, file.inputStream)
        return ResponseEntity.ok(CourseExtractDto(inserted))
    }

    @PostMapping("/sync")
    fun sync(
        @RequestBody req: CourseSyncRequestDto,
    ): ResponseEntity<CourseSyncResultDto> {
        val result = courseExtractService.syncFromSugang(req.year, req.semCode)
        return ResponseEntity.ok(result)
    }
}
