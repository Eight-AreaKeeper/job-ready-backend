package com.jobready.interviews;

import com.jobready.ai.AiClient;
import com.jobready.ai.AiModels.AnswerFeedback;
import com.jobready.ai.AiModels.InterviewAnswer;
import com.jobready.ai.AiModels.InterviewQuestion;
import com.jobready.common.ApiException;
import com.jobready.interviews.InterviewDtos.AnswerFeedbackDto;
import com.jobready.interviews.InterviewDtos.StartInterviewRequest;
import com.jobready.opportunities.Opportunity;
import com.jobready.opportunities.OpportunityService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Mock interview (TBN, stretch goal). Each graded answer is saved in interviews.answers. */
@Service
public class InterviewService {

    private final InterviewRepository interviews;
    private final OpportunityService opportunityService;
    private final AiClient ai;

    public InterviewService(InterviewRepository interviews, OpportunityService opportunityService, AiClient ai) {
        this.interviews = interviews;
        this.opportunityService = opportunityService;
        this.ai = ai;
    }

    @Transactional
    public Interview start(String userId, StartInterviewRequest req) {
        boolean hasOpp = req.opportunityId() != null && !req.opportunityId().isBlank();
        boolean hasRole = req.role() != null && !req.role().isBlank();
        if (hasOpp == hasRole) {
            throw ApiException.badRequest("send exactly one of opportunityId or role");
        }
        Opportunity opp = hasOpp ? opportunityService.get(req.opportunityId()) : null;
        String role = hasRole ? req.role() : opp.getTitle();
        List<InterviewQuestion> questions = ai.interviewQuestions(role, opp);
        return interviews.save(new Interview(userId, hasOpp ? opp.getId() : null, role, questions));
    }

    @Transactional
    public AnswerFeedbackDto answer(String userId, String interviewId, String questionId, String answer) {
        Interview interview = interviews.findById(interviewId)
                .filter(i -> i.getUserId().equals(userId))
                .orElseThrow(() -> ApiException.notFound("interview"));
        List<InterviewQuestion> qs = interview.getQuestions();
        int idx = -1;
        for (int i = 0; i < qs.size(); i++) {
            if (qs.get(i).id().equals(questionId)) {
                idx = i;
                break;
            }
        }
        if (idx < 0) {
            throw ApiException.notFound("question");
        }
        AnswerFeedback fb = ai.gradeAnswer(interview.getRole(), qs.get(idx).text(), answer);
        interview.recordAnswer(new InterviewAnswer(questionId, answer, fb.score(), fb.feedback()));
        interviews.save(interview);
        InterviewQuestion next = idx + 1 < qs.size() ? qs.get(idx + 1) : null;
        return new AnswerFeedbackDto(fb.score(), fb.feedback(), next);
    }
}
