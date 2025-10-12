CREATE TABLE IF NOT EXISTS course
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    year INT NOT NULL,
    semester VARCHAR(16) NOT NULL,
    category VARCHAR(64) NULL,
    college VARCHAR(64) NULL,
    department VARCHAR(64) NULL,
    program VARCHAR(64) NULL,
    grade INT NULL,
    courseNumber VARCHAR(64) NULL,
    classNumber VARCHAR(64) NULL,
    title VARCHAR(128) NOT NULL,
    credit INT NOT NULL,
    professor VARCHAR(64) NULL,
    room VARCHAR(64) NULL,

    UNIQUE KEY unique_course(year, semester, courseNumber, classNumber),
)

CREATE TABLE IF NOT EXISTS course_time(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    weekday INT NOT NULL,
    start_min INT NOT NULL,
    end_min INT NOT NULL,
    location VARCHAR(64) NULL,

    CONSTRAINT fk_course_id FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
)

CREATE TABLE IF NOT EXISTS timetable(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(64) NOT NULL,
    year INT NOT NULL,
    semester VARCHAR(16) NOT NULL,

    UNIQUE KEY unique_timetable(user_id, year, semester, name)
)

CREATE TABLE IF NOT EXISTS timetable_course(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    timetable_num BIGINT NOT NULL,
    course_id BIGINT NOT NULL,

    CONSTRAINT fk_timetable_num FOREIGN KEY (timetable_num) REFERENCES timetable(id) ON DELETE CASCADE,
    CONSTRAINT fk_course_id FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,

    UNIQUE KEY unique_timetable_course(timetable_num, course_id)
)