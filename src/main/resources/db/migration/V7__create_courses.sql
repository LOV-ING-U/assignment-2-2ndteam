CREATE TABLE IF NOT EXISTS course
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    `year` INT NOT NULL,
    semester VARCHAR(32) NOT NULL,
    category VARCHAR(32) NULL,
    college VARCHAR(32) NULL,
    department VARCHAR(128) NULL,
    program VARCHAR(32) NULL,
    grade INT NULL,
    courseNumber VARCHAR(32) NULL,
    classNumber VARCHAR(32) NULL,
    title VARCHAR(256) NOT NULL,
    subtitle VARCHAR(256) NULL,
    credit INT NOT NULL,
    professor VARCHAR(32) NULL,
    room VARCHAR(512) NULL,

    UNIQUE KEY unique_course(`year`, semester, courseNumber, classNumber)
);

CREATE TABLE IF NOT EXISTS course_time(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    weekday INT NOT NULL,
    start_min INT NOT NULL,
    end_min INT NOT NULL,
    location VARCHAR(512) NULL,

    CONSTRAINT fk_course_time_w_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS timetable(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    `name` VARCHAR(256) NOT NULL,
    `year` INT NOT NULL,
    semester VARCHAR(32) NOT NULL,

    UNIQUE KEY unique_timetable(user_id, `year`, semester, `name`)
);

CREATE TABLE IF NOT EXISTS timetable_course(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    timetable_num BIGINT NOT NULL,
    course_id BIGINT NOT NULL,

    CONSTRAINT fk_timetable_course_w_timetable FOREIGN KEY (timetable_num) REFERENCES timetable(id) ON DELETE CASCADE,
    CONSTRAINT fk_timetable_course_w_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,

    UNIQUE KEY unique_timetable_course(timetable_num, course_id)
);