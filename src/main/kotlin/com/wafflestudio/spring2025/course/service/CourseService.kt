package com.wafflestudio.spring2025.course.service

import com.wafflestudio.spring2025.course.CourseException
import com.wafflestudio.spring2025.course.dto.course.CourseDto
import com.wafflestudio.spring2025.course.dto.course.Paging
import com.wafflestudio.spring2025.course.dto.course.SearchCourseRequest
import com.wafflestudio.spring2025.course.dto.course.SearchCourseResponse
import com.wafflestudio.spring2025.course.dto.coursetime.CourseTimeDto
import com.wafflestudio.spring2025.course.repository.CourseRepository
import com.wafflestudio.spring2025.course.repository.CourseTimeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val courseTimeRepository: CourseTimeRepository,
) {
    @Transactional(readOnly = true)
    fun search(request: SearchCourseRequest): SearchCourseResponse {
        val limit = request.limit

        if (request.semester.isBlank()) {
            throw CourseException.InvalidSemesterException()
        }

        val ids =
            courseRepository.findIdsForSearch(
                year = request.year,
                semester = request.semester.trim(),
                keyword = request.keyword?.trim(),
                nextId = request.nextId,
                limit = limit + 1,
            )
        if (ids.isEmpty()) {
            return SearchCourseResponse(
                data = emptyList(),
                paging = Paging(hasNext = false, nextCursor = null),
            )
        }

        val hasNext = ids.size > limit
        val pageIds = if (hasNext) ids.take(limit) else ids
        val nextCursor = if (hasNext) pageIds.last() else null

        val courseMap = courseRepository.findByIds(pageIds).associateBy { it.id!! }
        val timeMap = courseTimeRepository.findByCourseIdIn(pageIds).groupBy { it.courseId }

        val data =
            pageIds.mapNotNull { id ->
                val c = courseMap[id] ?: return@mapNotNull null
                val times =
                    (timeMap[id] ?: emptyList()).map {
                        CourseTimeDto(
                            weekday = it.weekday,
                            startMin = it.startMin,
                            endMin = it.endMin,
                            location = it.location,
                        )
                    }
                CourseDto(
                    c = c,
                    times = times,
                )
            }

        return SearchCourseResponse(
            data = data,
            paging = Paging(hasNext = hasNext, nextCursor = nextCursor),
        )
    }
}
