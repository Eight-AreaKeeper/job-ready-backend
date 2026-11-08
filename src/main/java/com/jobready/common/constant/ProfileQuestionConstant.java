package com.jobready.common.constant;

import com.jobready.pojo.vo.ProfileQuestionVO;
import java.util.List;

/**
 * The profile questionnaire, grouped into sections. It merges our first draft with the
 * teammate's form (education, skills, experience and opportunity preference). Name, email
 * and city are not here: name and email come from the account, city is the request's
 * {@code location}, and industries are the request's {@code industries}.
 */
public final class ProfileQuestionConstant {

    private ProfileQuestionConstant() {
    }

    public static final String EDUCATION = "Education";
    public static final String SKILLS = "Skills";
    public static final String EXPERIENCE = "Experience";
    public static final String PREFERENCE = "Opportunity preference";

    public static final List<ProfileQuestionVO> QUESTIONS = List.of(
            q("edu_level", EDUCATION, "What is your highest level of education?", "single_select",
                    List.of("High school", "Diploma", "Bachelor's", "Master's", "PhD"), null),
            q("edu_school", EDUCATION, "College or university", "text", null, "University of Calgary"),
            q("edu_program", EDUCATION, "Program or major", "text", null, "Computer Science"),
            q("edu_year", EDUCATION, "Current year of study (or years worked, if you have graduated)", "text",
                    null, "3rd year"),
            q("edu_grad", EDUCATION, "Graduation year (expected or completed)", "text", null, "2027"),

            q("skills_tech", SKILLS, "Technical skills", "text", null, "Python, Excel, SQL"),
            q("skills_soft", SKILLS, "Soft skills", "text", null, "Communication, leadership, teamwork"),
            q("languages", SKILLS, "Languages you speak", "text", null, "English, Mandarin"),

            q("exp_jobs", EXPERIENCE, "Previous jobs (role, where, when, what you did)", "long_text", null,
                    "Barista, Phil & Sebastian, 2024 to now. Trained 3 new staff."),
            q("exp_volunteer", EXPERIENCE, "Volunteering", "long_text", null, null),
            q("exp_projects", EXPERIENCE,
                    "Projects, leadership or extracurriculars you are proud of", "long_text", null,
                    "Built a budgeting app with friends; led the robotics club."),
            q("certs", EXPERIENCE, "Certifications, awards or licenses", "text", null, "First Aid, Dean's List"),

            q("pref_roles", PREFERENCE, "Which roles are you interested in?", "text", null,
                    "Data analyst, software developer"),
            q("pref_env", PREFERENCE, "What kind of work environment do you prefer?", "single_select",
                    List.of("Startup", "Large company", "Research lab", "No preference"), null),
            q("pref_where", PREFERENCE, "Where are you willing to work?", "single_select",
                    List.of("Local", "Remote", "Abroad", "Any"), null),
            q("pref_availability", PREFERENCE, "What is your availability?", "single_select",
                    List.of("Full-time", "Part-time", "Internship or co-op", "Summer only"), null));

    private static ProfileQuestionVO q(String id, String section, String text, String type,
                                       List<String> options, String placeholder) {
        return ProfileQuestionVO.builder().id(id).section(section).text(text).type(type)
                .options(options).placeholder(placeholder).build();
    }
}
