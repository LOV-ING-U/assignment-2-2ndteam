package com.wafflestudio.spring2025.course.service

import com.wafflestudio.spring2025.course.CourseException
import com.wafflestudio.spring2025.course.dto.course.*
import com.wafflestudio.spring2025.course.dto.coursetime.CourseTimeDto
import com.wafflestudio.spring2025.course.repository.CourseRepository
import com.wafflestudio.spring2025.course.repository.CourseTimeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourseService (
    private val courseRepository: CourseRepository,
    private val courseTimeRepository: CourseTimeRepository
) {
    @Transactional(readOnly = true)
    fun search(request: SearchCourseRequest): SearchCourseResponse {
        val limit = request.limit

        if (request.semester.isBlank()) {
            throw CourseException.InvalidSemesterException()
        }

        val ids = courseRepository.findIdsForSearch(
            year = request.year,
            semester = request.semester.trim(),
            keyword = request.keyword?.trim(),
            nextId = request.nextId,
            limit = limit
        )
        if (ids.isEmpty()) {
            return SearchCourseResponse(emptyList(), null)
        }

        val courseMap = courseRepository.findByIds(ids).associateBy { it.id!! }
        val timeMap = courseTimeRepository.findByCourseIds(ids).groupBy { it.courseId }

        val items = ids.mapNotNull {
            id ->
            val c = courseMap[id] ?: return@mapNotNull null
            val times = (timeMap[id] ?: emptyList()).map {
                CourseTimeDto(
                    weekday = it.weekday,
                    startMin = it.startMin,
                    endMin = it.endMin,
                    location = it.location
                )
            }
            CourseDto(
                id = c.id!!,
                year = c.year,
                semester = c.semester,
                courseNumber = c.courseNumber,
                classNumber = c.classNumber,
                title = c.title,
                subtitle = c.subtitle,
                credit = c.credit,
                professor = c.professor,
                room = c.room,
                category = c.category,
                college = c.college,
                department = c.department,
                grade = c.grade,
                procedure = c.procedure,
                times = times
            )
        }

        val nextId = ids.lastOrNull()
        return SearchCourseResponse(items = items, nextId = nextId)
    }
}