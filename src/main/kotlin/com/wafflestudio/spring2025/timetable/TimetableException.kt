package com.wafflestudio.spring2025.timetable

import com.wafflestudio.spring2025.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

// @TODO

sealed class TimetableException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class TimetableNotFoundException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.NOT_FOUND,
        msg = "Timetable not found",
    )

class TimetableNameBlankException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.BAD_REQUEST,
        msg = "Timetable name is blank",
    )

class TimetableNameConflictException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.CONFLICT,
        msg = "Timetable name already exists",
    )

class TimetableUpdateForbiddenException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.FORBIDDEN,
        msg = "You are not allowed to modify or delete this timetable",
    )
