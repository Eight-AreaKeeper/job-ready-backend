package com.jobready.controller;

import com.jobready.common.result.ListResult;
import com.jobready.pojo.dto.MentorDTO;
import com.jobready.pojo.vo.MentorVO;
import com.jobready.service.MentorService;
import jakarta.validation.Valid;
import com.jobready.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mentors")
@Slf4j
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    /** Sign up (or update) as a mentor. Account role must be mentor. */
    @PostMapping
    public Result<MentorVO> signUp(@Valid @RequestBody MentorDTO dto) {
        log.info("Mentor signup: {}", dto);
        return Result.success(mentorService.signUp(dto));
    }

    /** GET /mentors?industry=&q= */
    @GetMapping
    public Result<ListResult<MentorVO>> list(@RequestParam(required = false) String industry,
            @RequestParam(required = false) String q) {
        return Result.success(ListResult.of(mentorService.list(industry, q)));
    }
}
