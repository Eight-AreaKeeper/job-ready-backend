package com.jobready.mentors;

import io.swagger.v3.oas.annotations.tags.Tag;
import com.jobready.common.ListResponse;
import com.jobready.mentors.MentorDtos.MentorDto;
import com.jobready.mentors.MentorDtos.MentorSignupRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mentors")
@RestController
@RequestMapping("/api/v1/mentors")
public class MentorsController {

    private final MentorService mentorService;

    public MentorsController(MentorService mentorService) {
        this.mentorService = mentorService;
    }

    /** POST /mentors -> 201 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MentorDto signUp(@AuthenticationPrincipal String userId, @Valid @RequestBody MentorSignupRequest req) {
        return mentorService.signUp(userId, req);
    }

    /** GET /mentors?industry=&q= */
    @GetMapping
    public ListResponse<MentorDto> browse(@RequestParam(required = false) String industry,
            @RequestParam(required = false) String q) {
        return ListResponse.of(mentorService.browse(industry, q));
    }
}
