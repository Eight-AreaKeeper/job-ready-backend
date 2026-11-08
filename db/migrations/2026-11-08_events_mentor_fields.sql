-- Run once on an existing job_ready_db created from an older db/schema.sql:
--   mysql -uroot -p job_ready_db < db/migrations/2026-11-08_events_mentor_fields.sql
-- (or open it in Navicat and run it). A fresh database made from db/schema.sql already has these.

USE job_ready_db;

-- 1. Events
CREATE TABLE IF NOT EXISTS events (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    host VARCHAR(255) NOT NULL,
    kind ENUM('career_fair', 'workshop', 'networking', 'info_session') NOT NULL,
    format ENUM('in_person', 'online') NOT NULL DEFAULT 'in_person',
    location VARCHAR(255),
    description TEXT,
    starts_at DATETIME NOT NULL,
    ends_at DATETIME NULL DEFAULT NULL,
    url VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_events_starts_at (starts_at)
);

-- 2. The schedule feature was removed. This deletes the table and every saved item in it.
DROP TABLE IF EXISTS schedule_items;

-- 3. Mentor photo and years of experience. This is last because MySQL cannot skip existing columns:
--    if you run the file twice, "Duplicate column name 'photo_url'" here just means it is already done.
ALTER TABLE mentors
    ADD COLUMN photo_url VARCHAR(512) DEFAULT NULL,
    ADD COLUMN years_experience INT UNSIGNED DEFAULT NULL;
