package com.jobready.resumes;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.jobready.common.ListResponse;
import com.jobready.resumes.ResumeDtos.ResumeDto;
import com.jobready.resumes.ResumeDtos.SuggestionsRequest;
import com.jobready.resumes.ResumeDtos.SuggestionsResponse;
import com.jobready.resumes.ResumeDtos.TailorRequest;
import com.jobready.resumes.ResumeDtos.TailorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Resumes")
@RestController
@RequestMapping("/api/v1/resumes")
public class ResumesController {

    private final ResumeService resumeService;

    public ResumesController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    /** GET /resumes */
    @GetMapping
    public ListResponse<ResumeDto> list(@AuthenticationPrincipal String userId) {
        return ListResponse.of(resumeService.list(userId).stream().map(ResumeDto::from).toList());
    }

    /** GET /resumes/{id} */
    @GetMapping("/{id}")
    public ResumeDto get(@AuthenticationPrincipal String userId, @PathVariable String id) {
        return ResumeDto.from(resumeService.get(userId, id));
    }

    /** POST /resumes/generate -> 201. Body can be empty. */
    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public ResumeDto generate(@AuthenticationPrincipal String userId) {
        return ResumeDto.from(resumeService.generate(userId));
    }

    /** POST /resumes/{id}/tailor -> 201 { resume, changes } */
    @PostMapping("/{id}/tailor")
    @ResponseStatus(HttpStatus.CREATED)
    public TailorResponse tailor(@AuthenticationPrincipal String userId, @PathVariable String id,
            @RequestBody TailorRequest req) {
        ResumeService.Tailored result = resumeService.tailor(userId, id, req);
        return new TailorResponse(ResumeDto.from(result.resume()), result.changes());
    }

    /** POST /resumes/{id}/suggestions -> { suggestions } */
    @PostMapping("/{id}/suggestions")
    public SuggestionsResponse suggestions(@AuthenticationPrincipal String userId, @PathVariable String id,
            @RequestBody(required = false) SuggestionsRequest req) {
        return new SuggestionsResponse(
                resumeService.suggestions(userId, id, req == null ? null : req.opportunityId()));
    }
}
