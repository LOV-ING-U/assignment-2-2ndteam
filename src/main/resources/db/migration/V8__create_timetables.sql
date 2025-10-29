CREATE TABLE IF NOT EXISTS timetables(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(64) NOT NULL,
    year INT NOT NULL,
    semester VARCHAR(16) NOT NULL,

    UNIQUE KEY unique_timetable(user_id, year, semester, name)
);

CREATE TABLE IF NOT EXISTS timetable_course(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    timetable_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,

    CONSTRAINT fk_timetable_course_w_timetable FOREIGN KEY (timetable_id) REFERENCES timetable(id) ON DELETE CASCADE,
    CONSTRAINT fk_timetable_course_w_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,

    UNIQUE KEY unique_timetable_course(timetable_id, course_id)
);