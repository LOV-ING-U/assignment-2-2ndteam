package com.wafflestudio.spring2025.timetable

import com.wafflestudio.spring2025.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

// @TODO

sealed class BoardException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class BoardNotFoundException :
    BoardException(
        errorCode = 0,
        httpStatusCode = HttpStatus.NOT_FOUND,
        msg = "Board not found",
    )

class BoardNameBlankException :
    BoardException(
        errorCode = 0,
        httpStatusCode = HttpStatus.BAD_REQUEST,
        msg = "Board name is blank",
    )

class BoardNameConflictException :
    BoardException(
        errorCode = 0,
        httpStatusCode = HttpStatus.CONFLICT,
        msg = "Board name already exists",
    )
