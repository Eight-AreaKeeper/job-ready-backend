package com.jobready.coffeechats;

import com.jobready.ai.AiClient;
import com.jobready.coffeechats.CoffeeChatDtos.CreateCoffeeChatRequest;
import com.jobready.coffeechats.CoffeeChatDtos.UpdateCoffeeChatRequest;
import com.jobready.common.ApiException;
import com.jobready.mentors.Mentor;
import com.jobready.mentors.MentorRepository;
import com.jobready.mentors.MentorService;
import com.jobready.profile.ProfileService;
import com.jobready.security.AppProperties;
import com.jobready.users.User;
import com.jobready.users.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoffeeChatService {

    private final CoffeeChatRepository chats;
    private final MentorRepository mentors;
    private final MentorService mentorService;
    private final UserService userService;
    private final ProfileService profileService;
    private final AiClient ai;
    private final AppProperties props;

    public CoffeeChatService(CoffeeChatRepository chats, MentorRepository mentors, MentorService mentorService,
            UserService userService, ProfileService profileService, AiClient ai, AppProperties props) {
        this.chats = chats;
        this.mentors = mentors;
        this.mentorService = mentorService;
        this.userService = userService;
        this.profileService = profileService;
        this.ai = ai;
        this.props = props;
    }

    /** POST /coffee-chats/icebreaker */
    public String icebreaker(String userId, String mentorId) {
        Mentor mentor = mentorService.get(mentorId);
        User mentorUser = userService.get(mentor.getUserId());
        return ai.icebreaker(userService.get(userId), profileService.find(userId).orElse(null), mentor, mentorUser);
    }

    /** POST /coffee-chats: student requests a chat; starts as pending. */
    @Transactional
    public CoffeeChat request(String userId, CreateCoffeeChatRequest req) {
        Mentor mentor = mentorService.get(req.mentorId());
        if (mentor.getUserId().equals(userId)) {
            throw ApiException.badRequest("you cannot request a chat with yourself");
        }
        // TODO(team): enforce maxChatsPerWeek once the rule is agreed.
        return chats.save(new CoffeeChat(userId, mentor.getId(), req.message(), req.proposedTimes()));
    }

    /** GET /coffee-chats: chats where I am the student, or the mentor. */
    public List<CoffeeChat> mine(String userId) {
        String myMentorId = mentors.findByUserId(userId).map(Mentor::getId).orElse("");
        return chats.findByStudentIdOrMentorId(userId, myMentorId);
    }

    /**
     * PATCH /coffee-chats/{id}: the mentor accepts or declines; either side can mark it completed.
     * Completing a chat adds points to the mentor.
     */
    @Transactional
    public CoffeeChat update(String userId, String chatId, UpdateCoffeeChatRequest req) {
        CoffeeChat chat = chats.findById(chatId).orElseThrow(() -> ApiException.notFound("coffee chat"));
        Mentor mentor = mentorService.get(chat.getMentorId());
        boolean isMentor = mentor.getUserId().equals(userId);
        boolean isStudent = chat.getStudentId().equals(userId);
        if (!isMentor && !isStudent) {
            throw ApiException.notFound("coffee chat");
        }

        switch (req.status()) {
            case "accepted", "declined" -> {
                if (!isMentor) {
                    throw ApiException.forbidden("only the mentor can accept or decline");
                }
                if (!"pending".equals(chat.getStatus())) {
                    throw ApiException.badRequest("only pending chats can be accepted or declined");
                }
            }
            case "completed" -> {
                if (!"accepted".equals(chat.getStatus())) {
                    throw ApiException.badRequest("only accepted chats can be completed");
                }
                mentor.addPoints(props.mentors().pointsPerCompletedChat());
                mentors.save(mentor);
            }
            default -> throw ApiException.badRequest("unknown status");
        }
        chat.setStatus(req.status());
        Optional.ofNullable(req.scheduledAt()).ifPresent(chat::setScheduledAt);
        return chats.save(chat);
    }
}
