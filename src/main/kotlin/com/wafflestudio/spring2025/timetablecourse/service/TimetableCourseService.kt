package com.wafflestudio.spring2025.timetablecourse.service

import com.wafflestudio.spring2025.course.dto.course.CourseDto
import com.wafflestudio.spring2025.course.dto.coursetime.CourseTimeDto
import com.wafflestudio.spring2025.timetablecourse.TimetableNotFoundException
import com.wafflestudio.spring2025.timetablecourse.CourseNotFoundException
import com.wafflestudio.spring2025.timetablecourse.TimetableAccessDeniedException
import com.wafflestudio.spring2025.timetablecourse.CourseTimeConflictException
import com.wafflestudio.spring2025.timetablecourse.CourseNotInTimetableException
import com.wafflestudio.spring2025.user.model.User
import com.wafflestudio.spring2025.timetable.repository.TimetableRepository
import com.wafflestudio.spring2025.course.repository.CourseRepository
import com.wafflestudio.spring2025.course.repository.CourseTimeRepository
import com.wafflestudio.spring2025.timetablecourse.dto.core.TimetableCourseDto
import com.wafflestudio.spring2025.timetablecourse.dto.core.TimetableDetailDto
import com.wafflestudio.spring2025.timetablecourse.model.TimetableCourse
import com.wafflestudio.spring2025.timetablecourse.repository.TimetableCourseRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.collections.emptyList

@Service
class TimetableCourseService(
    private val timetableCourseRepository: TimetableCourseRepository,
    private val timetableRepository: TimetableRepository,
    private val courseRepository: CourseRepository,
    private val courseTimeRepository: CourseTimeRepository
) {
    fun create(
        timetableId: Long,
        courseId: Long,
        user: User,
    ): TimetableCourseDto {
        val timetable = timetableRepository.findByIdOrNull(timetableId)
            ?: throw TimetableNotFoundException()
        val course = courseRepository.findByIdOrNull(courseId)
            ?: throw CourseNotFoundException()
        if (timetable.userId != user.id) throw TimetableAccessDeniedException()

        // 시간 중복 검증
        val newTimes = courseTimeRepository.findByCourseId(courseId) // 새로 추가하려는 강의 시간
        val existingCourseIds = timetableCourseRepository.findByTimetableId(timetableId)
            .map { it.courseId }
        val existingTimes = courseTimeRepository.findByCourseIdIn(existingCourseIds) // 기존 시간표에 있는 시간

        for (new in newTimes) {
            for (exist in existingTimes) {
                if (new.weekday == exist.weekday &&
                    !(new.endMin <= exist.startMin || new.startMin >= exist.endMin)
                ) {
                    throw CourseTimeConflictException()
                    // 같은 강의를 넣으려고 하는 경우 다른 에러를 띄우도록 할 수도 있는데, 어차피 똑같아서 이렇게 구현함
                }
            }
        }

        val timetableCourse =
            timetableCourseRepository.save(
                TimetableCourse(
                    timetableId = timetableId,
                    courseId = courseId,
                ),
            )

        val timeMap = courseTimeRepository.findByCourseId(courseId)
            val times = timeMap.map {
                CourseTimeDto(
                    weekday = it.weekday,
                    startMin = it.startMin,
                    endMin = it.endMin,
                    location = it.location
                )
            }

        return TimetableCourseDto(timetableCourse, course, times)
    }

    // 시간표 상세 조회
    fun getTimetableDetail(timetableId: Long, user: User): TimetableDetailDto {
        val timetable = timetableRepository.findByIdOrNull(timetableId)
            ?: throw TimetableNotFoundException()
        if (timetable.userId != user.id) throw TimetableAccessDeniedException()

        // 해당 시간표에 포함된 과목 ID 뽑기
        val timetableCourses = timetableCourseRepository.findByTimetableId(timetableId)
        val courseIds = timetableCourses.map { it.courseId }

        // 빈 시간표일 경우 예외처리
        if (courseIds.isEmpty()) {
            return TimetableDetailDto(
                timetable = timetable,
                totalCredit = 0,
                courses = emptyList(),
            )
        }

        // 모든 과목 찾고, 시간 정보도 빼기
        val courses = courseRepository.findAllById(courseIds)
        val allCourseTimes = courseTimeRepository.findByCourseIdIn(courseIds)
        val timesByCourse = allCourseTimes.groupBy { it.courseId }

        // DTO 조립: @TODO: Constructor가 구현되어 있다면 그걸로 바꾸기
        val courseDtos = courses.map { course ->
            val times = timesByCourse[course.id]?.map {
                CourseTimeDto(
                    weekday = it.weekday,
                    startMin = it.startMin,
                    endMin = it.endMin,
                    location = it.location
                )
            } ?: emptyList()

            CourseDto(
                c = course,
                times = times
            )
        }

        val totalCredit = courseDtos.sumOf { it.credit ?: 0 }

        return TimetableDetailDto(
            timetable = timetable,
            totalCredit = totalCredit,
            courses = courseDtos,
        )
    }

    fun delete(timetableId: Long, courseId: Long, user: User) {
        val timetable = timetableRepository.findByIdOrNull(timetableId)
            ?: throw TimetableNotFoundException()
        val course = courseRepository.findByIdOrNull(courseId)
            ?: throw CourseNotFoundException()
        if (timetable.userId != user.id) TimetableAccessDeniedException()
        if (!timetableCourseRepository.existsByTimetableIdAndCourseId(timetableId, courseId)) {
            throw CourseNotInTimetableException()
        }

        timetableCourseRepository.deleteByTimetableIdAndCourseId(timetableId, courseId)
    }
}