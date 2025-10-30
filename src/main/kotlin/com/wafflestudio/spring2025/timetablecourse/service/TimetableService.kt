package com.wafflestudio.spring2025.timetablecourse.service

// @TODO

import com.wafflestudio.spring2025.timetable.dto.core.TimetableDto
import com.wafflestudio.spring2025.timetable.model.Timetable

import com.wafflestudio.spring2025.user.model.User
import com.wafflestudio.spring2025.timetable.repository.TimetableRepositor
import com.wafflestudio.spring2025.course.repository.CourseRepository
import com.wafflestudio.spring2025.timetablecourse.dto.core.TimetableCourseDto
import com.wafflestudio.spring2025.timetablecourse.model.TimetableCourse
import com.wafflestudio.spring2025.timetablecourse.repository.TimetableCourseRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TimetableCourseService(
    private val timetableCourseRepository: TimetableCourseRepository,
    private val timetableRepository: TimetableRepository,
    private val courseRepository: CourseRepository,
) {
    fun create(
        timetableId: Long,
        courseId: Long,
        user: User,
    ): TimetableCourseDto {
        val course = courseRepository.findByIdOrNull(courseId)
            ?: throw //@TODO

        if (timetable.userId != user.id) throw //@TODO

        if (timetableCourseRepository.existsByTimetableIdAndCourseId(timetableId, courseId)) {
            throw //@TODO
        }
        
        // 이미 이 timetable에, 추가하려고 하는 course의 시간이 겹치는지 검증

        val timetableCourse =
            timetableCourseRepository.save(
                TimetableCourse(
                    timetableId = timetableId,
                    courseId = course.id,
                ),
            )
        return TimetableCourseDto(timetableCourse, course)
    }

}