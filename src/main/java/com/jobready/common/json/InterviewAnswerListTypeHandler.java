package com.jobready.common.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jobready.ai.AiModels.InterviewAnswer;
import java.util.List;

/** JSON column <-> List<InterviewAnswer>. */
public class InterviewAnswerListTypeHandler extends JsonTypeHandler<List<InterviewAnswer>> {

    public InterviewAnswerListTypeHandler() {
        super(new TypeReference<>() {});
    }
}
