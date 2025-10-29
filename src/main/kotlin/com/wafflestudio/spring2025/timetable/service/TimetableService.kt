package com.wafflestudio.spring2025.timetable.service

// @TODO

import com.wafflestudio.spring2025.board.BoardNameBlankException
import com.wafflestudio.spring2025.board.BoardNameConflictException
import com.wafflestudio.spring2025.board.BoardNotFoundException
import com.wafflestudio.spring2025.board.dto.core.BoardDto
import com.wafflestudio.spring2025.board.model.Board
import com.wafflestudio.spring2025.board.repository.BoardRepository
import com.wafflestudio.spring2025.comment.repository.CommentRepository
import com.wafflestudio.spring2025.post.PostBlankContentException
import com.wafflestudio.spring2025.post.PostBlankTitleException
import com.wafflestudio.spring2025.post.PostNotFoundException
import com.wafflestudio.spring2025.post.PostUpdateForbiddenException
import com.wafflestudio.spring2025.post.dto.PostPaging
import com.wafflestudio.spring2025.post.dto.PostPagingResponse
import com.wafflestudio.spring2025.post.dto.core.PostDto
import com.wafflestudio.spring2025.post.model.Post
import com.wafflestudio.spring2025.post.repository.PostRepository
import com.wafflestudio.spring2025.timetable.dto.core.TimetableDto
import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.timetable.repository.TimetableRepository
import com.wafflestudio.spring2025.user.model.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TimetableService(
    private val timetableRepository: TimetableRepository,
) {
    fun create(
        name: String,
        year: Int,
        semester: String,
        user: User,
    ): TimetableDto {
        val timetable =
            timetableRepository.save(
                Timetable(
                    name = name,
                    year = year,
                    semester = semester,
                    userId = user.id!!,
                ),
            )
        return TimetableDto(timetable, user)
    }

}