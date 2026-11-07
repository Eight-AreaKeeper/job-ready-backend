package com.jobready.controller;

import com.jobready.common.result.ListResult;
import com.jobready.pojo.dto.ResumeSuggestionDTO;
import com.jobready.pojo.dto.ResumeTailorDTO;
import com.jobready.pojo.entity.Resume;
import com.jobready.pojo.vo.ResumeSuggestionVO;
import com.jobready.pojo.vo.ResumeTailorVO;
import com.jobready.service.ResumeService;
import com.jobready.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resumes")
@Slf4j
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @GetMapping
    public Result<ListResult<Resume>> list() {
        return Result.success(ListResult.of(resumeService.list()));
    }

    @GetMapping("/{id}")
    public Result<Resume> getById(@PathVariable String id) {
        return Result.success(resumeService.getById(id));
    }

    /** AI writes a base resume from the career profile. Body can be empty. */
    @PostMapping("/generate")
    public Result<Resume> generate() {
        log.info("Generate base resume");
        return Result.success(resumeService.generate());
    }

    @PostMapping("/{id}/tailor")
    public Result<ResumeTailorVO> tailor(@PathVariable String id, @RequestBody ResumeTailorDTO dto) {
        log.info("Tailor resume {}: {}", id, dto);
        return Result.success(resumeService.tailor(id, dto));
    }

    @PostMapping("/{id}/suggestions")
    public Result<ResumeSuggestionVO> suggestions(@PathVariable String id,
            @RequestBody(required = false) ResumeSuggestionDTO dto) {
        return Result.success(resumeService.suggestions(id, dto == null ? null : dto.getOpportunityId()));
    }
}
