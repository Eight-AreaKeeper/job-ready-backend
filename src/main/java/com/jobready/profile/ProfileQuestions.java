package com.jobready.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jobready.common.ApiException;
import com.jobready.profile.ProfileDtos.AnswerInput;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The career-profile questionnaire. Each question feeds one profile field ({@link Field}),
 * so the AI (and the stub) know where an answer belongs. Industries and location are sent
 * as separate fields of POST /profile, not as questions.
 */
public final class ProfileQuestions {

    private ProfileQuestions() {
    }

    public static final int MAX_ANSWER_LENGTH = 2000;

    /** The profile field a question's answer is used for. */
    public enum Field { SKILLS, INTERESTS, EDUCATION, EXPERIENCE_LEVEL, EXPERIENCE, PROJECTS, PREFERRED_WORK_TYPE }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Question(String id, String text, String type, List<String> options, boolean required,
            String placeholder, @JsonIgnore Field field) {

        boolean isSingleSelect() {
            return "single_select".equals(type);
        }

        /** The option exactly as listed, matched case-insensitively. */
        Optional<String> matchOption(String answer) {
            return options == null ? Optional.empty()
                    : options.stream().filter(o -> o.equalsIgnoreCase(answer.trim())).findFirst();
        }
    }

    public static final List<Question> ALL = List.of(
            new Question("q1", "Which skills are you most confident in?", "text", null, true,
                    "e.g. Python, SQL, public speaking", Field.SKILLS),
            new Question("q2", "What topics or kinds of work interest you most?", "text", null, false,
                    "e.g. machine learning, renewable energy, product design", Field.INTERESTS),
            new Question("q3", "What are you studying, where, and when do you expect to finish?", "text", null, true,
                    "e.g. BSc Computer Science, University of Calgary, 2029", Field.EDUCATION),
            new Question("q4", "How would you describe your experience level?", "single_select",
                    List.of("Beginner", "Student", "Intermediate", "Senior"), true, null, Field.EXPERIENCE_LEVEL),
            new Question("q5", "Describe any work, internship or volunteer experience.", "text", null, false,
                    "Role, organization, dates, and what you did", Field.EXPERIENCE),
            new Question("q6", "Describe a project you are proud of.", "text", null, false,
                    "What you built, the tools you used, and the result", Field.PROJECTS),
            new Question("q7", "What kind of work environment do you prefer?", "single_select",
                    List.of("Startup", "Large company", "Research lab", "Non-profit", "Government"), true, null,
                    Field.PREFERRED_WORK_TYPE));

    private static final Map<String, Question> BY_ID = new LinkedHashMap<>();

    static {
        ALL.forEach(q -> BY_ID.put(q.id(), q));
    }

    public static Optional<Question> find(String id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    /**
     * Checks the answers against the questionnaire and returns them keyed by field, trimmed,
     * with single-select answers normalised to the listed option. Throws 400 on the first problem.
     */
    public static Map<Field, String> validate(List<AnswerInput> answers) {
        Map<Field, String> byField = new LinkedHashMap<>();
        Set<String> seen = new HashSet<>();
        for (AnswerInput a : answers) {
            Question q = find(a.questionId())
                    .orElseThrow(() -> ApiException.badRequest("unknown questionId " + a.questionId()));
            if (!seen.add(q.id())) {
                throw ApiException.badRequest(q.id() + " is answered more than once");
            }
            String value = a.answer() == null ? "" : a.answer().trim();
            if (value.isEmpty()) {
                continue;
            }
            if (value.length() > MAX_ANSWER_LENGTH) {
                throw ApiException.badRequest(q.id() + " must be at most " + MAX_ANSWER_LENGTH + " characters");
            }
            if (q.isSingleSelect()) {
                value = q.matchOption(value).orElseThrow(() -> ApiException.badRequest(
                        q.id() + " must be one of " + String.join(", ", q.options())));
            }
            byField.put(q.field(), value);
        }
        for (Question q : ALL) {
            if (q.required() && !byField.containsKey(q.field())) {
                throw ApiException.badRequest(q.id() + " is required");
            }
        }
        return byField;
    }
}
