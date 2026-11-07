package com.jobready.common.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jobready.ai.AiModels.InterviewQuestion;
import java.util.List;

/** JSON column <-> List<InterviewQuestion>. */
public class InterviewQuestionListTypeHandler extends JsonTypeHandler<List<InterviewQuestion>> {

    public InterviewQuestionListTypeHandler() {
        super(new TypeReference<>() {});
    }
}
