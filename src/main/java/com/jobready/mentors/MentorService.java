package com.jobready.mentors;

import com.jobready.common.ApiException;
import com.jobready.mentors.MentorDtos.MentorDto;
import com.jobready.mentors.MentorDtos.MentorSignupRequest;
import com.jobready.users.User;
import com.jobready.users.UserRepository;
import com.jobready.users.UserService;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MentorService {

    private final MentorRepository mentors;
    private final UserRepository users;
    private final UserService userService;

    public MentorService(MentorRepository mentors, UserRepository users, UserService userService) {
        this.mentors = mentors;
        this.users = users;
        this.userService = userService;
    }

    public Mentor get(String mentorId) {
        return mentors.findById(mentorId).orElseThrow(() -> ApiException.notFound("mentor"));
    }

    /** POST /mentors: creates or updates the caller's mentor card. Account role must be mentor. */
    @Transactional
    public MentorDto signUp(String userId, MentorSignupRequest req) {
        User user = userService.get(userId);
        if (!user.isMentor()) {
            throw ApiException.forbidden("only accounts with role mentor can sign up as a mentor");
        }
        Mentor m = mentors.findByUserId(userId).orElseGet(() -> new Mentor(userId));
        m.setHeadline(req.headline());
        m.setCompany(req.company());
        m.setIndustries(req.industries() == null ? List.of() : req.industries());
        m.setExpertise(req.expertise() == null ? List.of() : req.expertise());
        m.setAvailability(req.availability() == null ? List.of() : req.availability());
        m.setMaxChatsPerWeek(req.maxChatsPerWeek());
        return MentorDto.from(mentors.save(m), user.getName());
    }

    /** GET /mentors?industry=&q= (in-memory filter is fine at hackathon scale). */
    public List<MentorDto> browse(String industry, String q) {
        List<Mentor> all = mentors.findAll();
        Map<String, User> byId = users.findAllById(all.stream().map(Mentor::getUserId).toList()).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        String needle = q == null ? null : q.toLowerCase(Locale.ROOT);
        return all.stream()
                .filter(m -> industry == null
                        || m.getIndustries().stream().anyMatch(i -> i.equalsIgnoreCase(industry)))
                .map(m -> MentorDto.from(m, byId.containsKey(m.getUserId()) ? byId.get(m.getUserId()).getName() : null))
                .filter(d -> needle == null || String.join(" ", d.name() == null ? "" : d.name(),
                        d.headline() == null ? "" : d.headline(), d.company() == null ? "" : d.company(),
                        String.join(" ", d.expertise())).toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }
}
