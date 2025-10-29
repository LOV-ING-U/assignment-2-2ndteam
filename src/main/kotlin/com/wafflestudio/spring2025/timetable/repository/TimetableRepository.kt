package com.wafflestudio.spring2025.timetable.repository

import com.wafflestudio.spring2025.timetable.model.Timetable
import org.springframework.data.repository.ListCrudRepository

interface TimetableRepository : ListCrudRepository<Timetable, Long> {
    fun existsByName(name: String): Boolean
    // 아마 기능 구현할 때 함수 더 추가해야 할듯
}
