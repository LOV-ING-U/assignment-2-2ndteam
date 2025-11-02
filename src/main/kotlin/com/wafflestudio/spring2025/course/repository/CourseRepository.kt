package com.wafflestudio.spring2025.course.repository

import com.wafflestudio.spring2025.course.model.Course
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface CourseRepository : CrudRepository<Course, Long> {
    @Query(
        """
        SELECT c.id
        FROM courses c
        WHERE c.year = :year
            AND c.semester = :semester
            AND (
                :keyword IS NULL OR :keyword = '' OR
                c.title LIKE CONCAT('%', :keyword, '%') OR
                c.professor LIKE CONCAT('%', :keyword, '%')
            )
            AND (:nextId IS NULL OR c.id > :nextId)
        ORDER BY c.id ASC
        LIMIT :limit
    """,
    )
    fun findIdsForSearch(
        @Param("year") year: Int,
        @Param("semester") semester: String,
        @Param("keyword") keyword: String?,
        @Param("nextId") nextId: Long?,
        @Param("limit") limit: Int,
    ): List<Long>

    @Query("SELECT * FROM courses WHERE id IN (:ids)")
    fun findByIds(
        @Param("ids") ids: List<Long>,
    ): List<Course>
}
