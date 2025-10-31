package com.wafflestudio.spring2025.timetablecourse

import com.wafflestudio.spring2025.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class TimetableCourseException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class CourseNotFoundException :
    TimetableCourseException(
        errorCode = 0,
        httpStatusCode = HttpStatus.NOT_FOUND,
        msg = "Course not found",
    )

class TimetableNotFoundException :
    TimetableCourseException(
        errorCode = 0,
        httpStatusCode = HttpStatus.NOT_FOUND,
        msg = "Timetable not found",
    )

class TimetableAccessDeniedException :
    TimetableCourseException(
        errorCode = 0,
        httpStatusCode = HttpStatus.FORBIDDEN,
        msg = "You do not have permission to access this timetable",
    )

class CourseTimeConflictException :
    TimetableCourseException(
        errorCode = 0,
        httpStatusCode = HttpStatus.CONFLICT,
        msg = "Course time overlaps with another course in timetable",
    )

class CourseNotInTimetableException :
    TimetableCourseException(
        errorCode = 0,
        httpStatusCode = HttpStatus.NOT_FOUND,
        msg = "Course not found in timetable",
    )
