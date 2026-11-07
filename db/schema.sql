-- job_ready MySQL schema (based on the team's schema.sql, 2026-11-07).
-- Changes from the uploaded version are marked "FIX" or "ADDED"; see README "Database".

CREATE DATABASE IF NOT EXISTS job_ready_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE job_ready_db;

-- 1. Users
CREATE TABLE users (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('student', 'mentor') NOT NULL DEFAULT 'student',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Profiles
-- FIX: missing commas, "educational information" -> education, location given a type.
-- ADDED back (were commented out / dropped): experience_level, preferred_work_type,
--   industries (POST /profile and matching use it), updated_at.
CREATE TABLE profiles (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    summary TEXT,
    skills JSON,
    education JSON,            -- [{ school, degree, end }]
    projects JSON,             -- [{ name, bullets[] }]
    interests JSON,
    experience JSON,           -- [{ title, org, start, end, bullets[] }]
    industries JSON,           -- up to 3
    preferred_work_type VARCHAR(64),
    experience_level ENUM('beginner', 'student', 'intermediate', 'senior'),
    location VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. profile_answers: removed in this version. Questionnaire answers go straight to the AI
--    and are not stored.

-- 4. Opportunities
CREATE TABLE opportunities (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    organization VARCHAR(255) NOT NULL,
    type ENUM('job', 'internship', 'research') NOT NULL,
    industry VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL,
    scope ENUM('local', 'remote', 'abroad') NOT NULL DEFAULT 'local',
    description TEXT,
    requirements JSON,
    deadline DATE,
    url VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Schedule items
-- ADDED: unique (user_id, opportunity_id) so the same opportunity can't be saved twice.
CREATE TABLE schedule_items (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    opportunity_id VARCHAR(64) NOT NULL,
    status ENUM('saved', 'applied', 'interview', 'offer', 'closed') NOT NULL DEFAULT 'saved',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_schedule_user_opportunity UNIQUE (user_id, opportunity_id),
    CONSTRAINT fk_schedule_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_schedule_opportunity FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE
);

-- 6. Resumes
CREATE TABLE resumes (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    opportunity_id VARCHAR(64) DEFAULT NULL,
    title VARCHAR(255) NOT NULL,
    sections JSON NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resumes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_resumes_opportunity FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE SET NULL
);

-- 7. Mentors
CREATE TABLE mentors (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    headline VARCHAR(255) NOT NULL,
    company VARCHAR(255),
    industries JSON,
    expertise JSON,
    availability JSON,
    max_chats_per_week INT UNSIGNED DEFAULT 2,
    points INT UNSIGNED NOT NULL DEFAULT 0,
    CONSTRAINT fk_mentors_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 8. Coffee chats (mentor_id references mentors.id, not users.id)
-- FIX: scheduled_at DATETIME instead of TIMESTAMP (TIMESTAMP stops at 2038 and shifts with the
--      session time zone). The app stores it in UTC.
CREATE TABLE coffee_chats (
    id VARCHAR(64) PRIMARY KEY,
    student_id VARCHAR(64) NOT NULL,
    mentor_id VARCHAR(64) NOT NULL,
    status ENUM('pending', 'accepted', 'declined', 'completed') NOT NULL DEFAULT 'pending',
    message TEXT,
    proposed_times JSON,
    scheduled_at DATETIME NULL DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chats_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_chats_mentor FOREIGN KEY (mentor_id) REFERENCES mentors(id) ON DELETE CASCADE
);

-- 9. Interviews (stretch goal)
-- ADDED: role, so an interview started with { "role": "Data Analyst" } (no opportunity) keeps its role.
CREATE TABLE interviews (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    opportunity_id VARCHAR(64) DEFAULT NULL,
    role VARCHAR(255),
    questions JSON,
    answers JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_interviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_interviews_opportunity FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE SET NULL
);

-- Indexes
CREATE INDEX idx_opportunities_type_scope ON opportunities(type, scope);
CREATE INDEX idx_opportunities_industry ON opportunities(industry);
-- FIX: was idx_schedule_user_deadline on (user_id, status); schedule_items has no deadline column.
CREATE INDEX idx_schedule_user_status ON schedule_items(user_id, status);
CREATE INDEX idx_coffee_chats_student ON coffee_chats(student_id);
CREATE INDEX idx_coffee_chats_mentor ON coffee_chats(mentor_id);
