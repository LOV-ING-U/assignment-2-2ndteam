package com.wafflestudio.spring2025.course.extract.service

import com.wafflestudio.spring2025.course.extract.repository.CourseTimeExtractRepository
import org.springframework.stereotype.Service

@Service
class CourseTimeExtractService (
    private val courseTimeExtractRepository: CourseTimeExtractRepository
){
}