package com.jobready.interviews;

import com.jobready.interviews.InterviewDtos.AnswerFeedbackDto;
import com.jobready.interviews.InterviewDtos.AnswerRequest;
import com.jobready.interviews.InterviewDtos.InterviewDto;
import com.jobready.interviews.InterviewDtos.StartInterviewRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Mock interview (TBN, stretch goal). */
@RestController
@RequestMapping("/api/v1/interviews")
public class InterviewsController {

    private final InterviewService interviewService;

    public InterviewsController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    /** POST /interviews -> 201 { id, questions } */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewDto start(@AuthenticationPrincipal String userId, @RequestBody StartInterviewRequest req) {
        Interview i = interviewService.start(userId, req);
        return new InterviewDto(i.getId(), i.getQuestions());
    }

    /** POST /interviews/{id}/answers -> { score, feedback, nextQuestion } */
    @PostMapping("/{id}/answers")
    public AnswerFeedbackDto answer(@AuthenticationPrincipal String userId, @PathVariable String id,
            @Valid @RequestBody AnswerRequest req) {
        return interviewService.answer(userId, id, req.questionId(), req.answer());
    }
}
