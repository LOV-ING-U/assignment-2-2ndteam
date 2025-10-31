package com.wafflestudio.spring2025.timetable.service

// @TODO

import com.wafflestudio.spring2025.timetable.TimetableNameBlankException
import com.wafflestudio.spring2025.timetable.TimetableNameConflictException
import com.wafflestudio.spring2025.timetable.TimetableNotFoundException
import com.wafflestudio.spring2025.timetable.TimetableUpdateForbiddenException
import com.wafflestudio.spring2025.timetable.dto.core.TimetableDto
import com.wafflestudio.spring2025.timetable.model.Semester
import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.timetable.repository.TimetableRepository
import com.wafflestudio.spring2025.user.model.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TimetableService(
    private val timetableRepository: TimetableRepository,
) {
    fun create(
        user: User,
        name: String,
        year: Int,
        semester: Semester,
    ): TimetableDto {
        if (name.isBlank()) {
            throw TimetableNameBlankException()
        }
        if (timetableRepository.existsByUserIdAndName(user.id!!, name)) {
            throw TimetableNameConflictException()
        }
        val timetable =
            timetableRepository.save(
                Timetable(
                    name = name,
                    year = year,
                    semester = semester,
                    userId = user.id!!,
                ),
            )
        return TimetableDto(timetable)
    }

    fun list(user: User): List<TimetableDto> {
        val timetables = timetableRepository.findAllByUserId(user.id!!)
        return timetables.map { TimetableDto(it) }
    }

    fun update(
        timetableId: Long,
        name: String?,
        user: User,
    ): TimetableDto {
        if (name?.isBlank() == true) {
            throw TimetableNameBlankException()
        }

        val timetable =
            timetableRepository.findByIdOrNull(timetableId)
                ?: throw TimetableNotFoundException()

        if (timetable.userId != user.id) {
            throw TimetableUpdateForbiddenException()
        }

        if (
            name != null &&
            name != timetable.name &&
            timetableRepository.existsByUserIdAndName(timetable.userId, name)
        ) {
            throw TimetableNameConflictException()
        }

        name?.let { timetable.name = it }
        timetableRepository.save(timetable)

        val updated =
            timetableRepository.findByIdOrNull(timetable.id!!)
                ?: throw TimetableNotFoundException()
        return TimetableDto(updated)
    }

    fun delete(
        timetableId: Long,
        user: User,
    ) {
        val timetable =
            timetableRepository.findByIdOrNull(timetableId)
                ?: throw TimetableNotFoundException()

        if (timetable.userId != user.id) {
            throw TimetableUpdateForbiddenException()
        }

        // ### for deleting all information about timetable
        // TODO : lectureRepository.deleteByTimetableId(timetableId)

        timetableRepository.delete(timetable)
    }
}
