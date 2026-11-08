package com.jobready.common.constant;

/** Fixed values used across modules (match the ENUM columns in db/job_ready_db.sql). */
public final class StatusConstant {

    private StatusConstant() {
    }

    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_MENTOR = "mentor";

    public static final String CHAT_PENDING = "pending";
    public static final String CHAT_ACCEPTED = "accepted";
    public static final String CHAT_DECLINED = "declined";
    public static final String CHAT_COMPLETED = "completed";

    public static final String EXPERIENCE_LEVEL_PATTERN = "beginner|student|intermediate|senior";
    public static final String OPPORTUNITY_TYPE_PATTERN = "job|internship|research";
    public static final String SCOPE_PATTERN = "local|remote|abroad";
    public static final String EVENT_KIND_PATTERN = "career_fair|workshop|networking|info_session";
    public static final String EVENT_FORMAT_PATTERN = "in_person|online";
}
