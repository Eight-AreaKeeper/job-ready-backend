package com.jobready.service.impl;

import com.jobready.ai.AiClient;
import com.jobready.ai.AiModels.AnswerFeedback;
import com.jobready.ai.AiModels.InterviewAnswer;
import com.jobready.ai.AiModels.InterviewQuestion;
import com.jobready.common.context.BaseContext;
import com.jobready.common.exception.BaseException;
import com.jobready.common.utils.IdUtil;
import com.jobready.mapper.InterviewMapper;
import com.jobready.pojo.dto.InterviewAnswerDTO;
import com.jobready.pojo.dto.InterviewStartDTO;
import com.jobready.pojo.entity.Interview;
import com.jobready.pojo.entity.Opportunity;
import com.jobready.pojo.vo.InterviewFeedbackVO;
import com.jobready.pojo.vo.InterviewVO;
import com.jobready.service.InterviewService;
import com.jobready.service.OpportunityService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Mock interview (stretch goal). Each graded answer is saved in interviews.answers. */
@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewMapper interviewMapper;
    private final OpportunityService opportunityService;
    private final AiClient aiClient;

    @Override
    @Transactional
    public InterviewVO start(InterviewStartDTO dto) {
        boolean hasOpp = dto.getOpportunityId() != null && !dto.getOpportunityId().isBlank();
        boolean hasRole = dto.getRole() != null && !dto.getRole().isBlank();
        if (hasOpp == hasRole) {
            throw BaseException.badRequest("send exactly one of opportunityId or role");
        }
        Opportunity opp = hasOpp ? opportunityService.getById(dto.getOpportunityId()) : null;
        String role = hasRole ? dto.getRole() : opp.getTitle();
        Interview interview = Interview.builder()
                .id(IdUtil.newId("i"))
                .userId(BaseContext.getCurrentId())
                .opportunityId(opp == null ? null : opp.getId())
                .role(role)
                .questions(aiClient.interviewQuestions(role, opp))
                .answers(List.of())
                .createdAt(Instant.now())
                .build();
        interviewMapper.insert(interview);
        return new InterviewVO(interview.getId(), interview.getQuestions());
    }

    @Override
    @Transactional
    public InterviewFeedbackVO answer(String id, InterviewAnswerDTO dto) {
        Interview interview = interviewMapper.getById(id);
        if (interview == null || !interview.getUserId().equals(BaseContext.getCurrentId())) {
            throw BaseException.notFound("interview");
        }
        List<InterviewQuestion> qs = interview.getQuestions();
        int idx = -1;
        for (int i = 0; i < qs.size(); i++) {
            if (qs.get(i).id().equals(dto.getQuestionId())) {
                idx = i;
                break;
            }
        }
        if (idx < 0) {
            throw BaseException.notFound("question");
        }
        AnswerFeedback fb = aiClient.gradeAnswer(interview.getRole(), qs.get(idx).text(), dto.getAnswer());

        // Replace any earlier answer to the same question.
        List<InterviewAnswer> answers = new ArrayList<>(
                interview.getAnswers() == null ? List.of() : interview.getAnswers());
        answers.removeIf(a -> a.questionId().equals(dto.getQuestionId()));
        answers.add(new InterviewAnswer(dto.getQuestionId(), dto.getAnswer(), fb.score(), fb.feedback()));
        interview.setAnswers(answers);
        interviewMapper.updateAnswers(interview);

        InterviewQuestion next = idx + 1 < qs.size() ? qs.get(idx + 1) : null;
        return new InterviewFeedbackVO(fb.score(), fb.feedback(), next);
    }
}
