package com.jobready.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * The questionnaire. TODO(team): the final question list is still to be decided;
 * these are placeholders so the front end has something to render.
 */
public final class ProfileQuestions {

    private ProfileQuestions() {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Question(String id, String text, String type, List<String> options) {
    }

    public static final List<Question> ALL = List.of(
            new Question("q1", "Which skills are you most confident in?", "text", null),
            new Question("q2", "What kind of work environment do you prefer?", "single_select",
                    List.of("Startup", "Large company", "Research lab")),
            new Question("q3", "What year of study are you in, or how many years have you worked?", "text", null),
            new Question("q4", "Describe a project you are proud of.", "text", null),
            new Question("q5", "Where are you willing to work?", "single_select",
                    List.of("Local", "Remote", "Abroad", "Any")));
}
