package com.jobready.common.constant;

import com.jobready.pojo.vo.ProfileQuestionVO;
import java.util.List;

/**
 * The questionnaire. TODO(team): the final question list is still to be decided;
 * these are placeholders so the front end has something to render.
 */
public final class ProfileQuestionConstant {

    private ProfileQuestionConstant() {
    }

    public static final List<ProfileQuestionVO> QUESTIONS = List.of(
            new ProfileQuestionVO("q1", "Which skills are you most confident in?", "text", null),
            new ProfileQuestionVO("q2", "What kind of work environment do you prefer?", "single_select",
                    List.of("Startup", "Large company", "Research lab")),
            new ProfileQuestionVO("q3", "What year of study are you in, or how many years have you worked?",
                    "text", null),
            new ProfileQuestionVO("q4", "Describe a project you are proud of.", "text", null),
            new ProfileQuestionVO("q5", "Where are you willing to work?", "single_select",
                    List.of("Local", "Remote", "Abroad", "Any")));
}
