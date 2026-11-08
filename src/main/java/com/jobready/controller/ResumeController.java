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
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    /**
     * AI writes a new resume from the user's profile for one job.
     * Body: { "opportunityId": "o_..." } or { "jobDescription": "..." }.
     * Download it afterwards with GET /resumes/{id}/pdf.
     */
    @PostMapping("/tailor")
    public Result<ResumeTailorVO> tailor(@RequestBody ResumeTailorDTO dto) {
        log.info("Tailor resume: opportunityId={}, jobDescription={} chars", dto.getOpportunityId(),
                dto.getJobDescription() == null ? 0 : dto.getJobDescription().length());
        return Result.success(resumeService.tailor(dto));
    }

    /** The resume as a PDF file (not wrapped in Result). */
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable String id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename("resume-" + id + ".pdf").build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resumeService.pdf(id));
    }

    @PostMapping("/{id}/suggestions")
    public Result<ResumeSuggestionVO> suggestions(@PathVariable String id,
            @RequestBody(required = false) ResumeSuggestionDTO dto) {
        return Result.success(resumeService.suggestions(id, dto == null ? null : dto.getOpportunityId()));
    }
}
