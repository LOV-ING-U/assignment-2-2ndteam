package com.wafflestudio.spring2025.course

import com.wafflestudio.spring2025.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

open class CourseException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
) : DomainException(errorCode, httpStatusCode, msg) {
    class InvalidSemesterException :
        CourseException(
            errorCode = 0,
            httpStatusCode = HttpStatus.BAD_REQUEST,
            msg = "Invalid semester",
        )

    class CourseNotFoundException :
        CourseException(
            errorCode = 0,
            httpStatusCode = HttpStatus.NOT_FOUND,
            msg = "Course not found",
        )
}
