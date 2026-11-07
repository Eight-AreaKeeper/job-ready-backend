package com.jobready.controller;

import com.jobready.pojo.dto.InterviewAnswerDTO;
import com.jobready.pojo.dto.InterviewStartDTO;
import com.jobready.pojo.vo.InterviewFeedbackVO;
import com.jobready.pojo.vo.InterviewVO;
import com.jobready.service.InterviewService;
import jakarta.validation.Valid;
import com.jobready.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/** Mock interview (stretch goal). */
@RestController
@RequestMapping("/api/v1/interviews")
@Slf4j
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public Result<InterviewVO> start(@RequestBody InterviewStartDTO dto) {
        log.info("Start mock interview: {}", dto);
        return Result.success(interviewService.start(dto));
    }

    @PostMapping("/{id}/answers")
    public Result<InterviewFeedbackVO> answer(@PathVariable String id, @Valid @RequestBody InterviewAnswerDTO dto) {
        return Result.success(interviewService.answer(id, dto));
    }
}
