package com.wafflestudio.spring2025.helper

import com.wafflestudio.spring2025.board.model.Board
import com.wafflestudio.spring2025.board.repository.BoardRepository
import com.wafflestudio.spring2025.comment.model.Comment
import com.wafflestudio.spring2025.comment.repository.CommentRepository
import com.wafflestudio.spring2025.course.model.Course
import com.wafflestudio.spring2025.course.model.CourseTime
import com.wafflestudio.spring2025.course.repository.CourseRepository
import com.wafflestudio.spring2025.course.repository.CourseTimeRepository
import com.wafflestudio.spring2025.post.model.Post
import com.wafflestudio.spring2025.post.repository.PostRepository
import com.wafflestudio.spring2025.timetable.model.Semester
import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.timetable.repository.TimetableRepository
import com.wafflestudio.spring2025.timetablecourse.model.TimetableCourse
import com.wafflestudio.spring2025.timetablecourse.repository.TimetableCourseRepository
import com.wafflestudio.spring2025.user.JwtTokenProvider
import com.wafflestudio.spring2025.user.model.User
import com.wafflestudio.spring2025.user.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class DataGenerator(
    private val userRepository: UserRepository,
    private val boardRepository: BoardRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val courseRepository: CourseRepository,
    private val timetableRepository: TimetableRepository,
    private val timetableCourseRepository: TimetableCourseRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val courseTimeRepository: CourseTimeRepository,
) {
    fun generateUser(
        username: String? = null,
        password: String? = null,
    ): Pair<User, String> {
        val user =
            userRepository.save(
                User(
                    username = username ?: "user-${Random.Default.nextInt(1000000)}",
                    password = BCrypt.hashpw(password ?: "password-${Random.Default.nextInt(1000000)}", BCrypt.gensalt()),
                ),
            )
        return user to jwtTokenProvider.createToken(user.username)
    }

    fun generateBoard(name: String? = null): Board {
        val board =
            boardRepository.save(
                Board(
                    name = name ?: "board-${Random.Default.nextInt(1000000)}",
                ),
            )
        return board
    }

    fun generatePost(
        title: String? = null,
        content: String? = null,
        user: User? = null,
        board: Board? = null,
    ): Post {
        val post =
            postRepository.save(
                Post(
                    title = title ?: "title-${Random.Default.nextInt(1000000)}",
                    content = content ?: "content-${Random.Default.nextInt(1000000)}",
                    userId = (user ?: generateUser().first).id!!,
                    boardId = (board ?: generateBoard()).id!!,
                ),
            )
        return post
    }

    fun generateComment(
        content: String? = null,
        user: User? = null,
        post: Post? = null,
    ): Comment {
        val comment =
            commentRepository.save(
                Comment(
                    content = content ?: "content-${Random.Default.nextInt(1000000)}",
                    userId = (user ?: generateUser().first).id!!,
                    postId = (post ?: generatePost()).id!!,
                ),
            )
        return comment
    }

    fun generateTimetable(
        user: User,
        name: String = "Default Timetable",
        year: Int = 2025,
        semester: Semester = Semester.SPRING,
    ): Timetable =
        timetableRepository.save(
            Timetable(
                userId = user.id!!,
                name = name,
                year = year,
                semester = semester,
            ),
        )

    fun generateCourse(
        title: String? = null,
        credit: Int = 3,
        weekday: Int = 1,
        startMin: Int = 600,
        endMin: Int = 660,
        professor: String? = null,
    ): Course {
        val course =
            courseRepository.save(
                Course(
                    year = 2025,
                    semester = "FALL",
                    title = title ?: "테스트 강의-${Random.nextInt(100000)}",
                    credit = credit,
                    professor = professor ?: "교수-${Random.nextInt(1000)}",
                    room = "302-${Random.nextInt(100)}",
                    category = "전필",
                    college = "공과대학",
                    department = "컴퓨터공학부",
                    grade = 3,
                    courseNumber = "CS${System.nanoTime()}",
                    classNumber = "00${Random.nextInt(9)}",
                    procedure = "학부",
                    subtitle = "",
                ),
            )

        // course_time 도 같이 생성
        courseTimeRepository.save(
            CourseTime(
                courseId = course.id!!,
                weekday = weekday,
                startMin = startMin,
                endMin = endMin,
                location = course.room,
            ),
        )

        return course
    }

    fun addCourseToTimetable(
        timetable: Timetable,
        course: Course,
    ): TimetableCourse {
        val timetableCourse =
            timetableCourseRepository.save(
                TimetableCourse(
                    timetableId = timetable.id!!,
                    courseId = course.id!!,
                ),
            )
        return timetableCourse
    }
}
