package com.jobready.service.impl;

import com.jobready.ai.AiClient;
import com.jobready.common.constant.StatusConstant;
import com.jobready.common.context.BaseContext;
import com.jobready.common.exception.BaseException;
import com.jobready.common.properties.AppProperties;
import com.jobready.common.utils.IdUtil;
import com.jobready.mapper.CoffeeChatMapper;
import com.jobready.mapper.MentorMapper;
import com.jobready.pojo.dto.CoffeeChatDTO;
import com.jobready.pojo.dto.CoffeeChatUpdateDTO;
import com.jobready.pojo.entity.CoffeeChat;
import com.jobready.pojo.entity.Mentor;
import com.jobready.service.CoffeeChatService;
import com.jobready.service.MentorService;
import com.jobready.service.ProfileService;
import com.jobready.service.UserService;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeeChatServiceImpl implements CoffeeChatService {

    private final CoffeeChatMapper coffeeChatMapper;
    private final MentorMapper mentorMapper;
    private final MentorService mentorService;
    private final UserService userService;
    private final ProfileService profileService;
    private final AiClient aiClient;
    private final AppProperties props;

    @Override
    public String icebreaker(String mentorId) {
        String userId = BaseContext.getCurrentId();
        Mentor mentor = mentorService.getById(mentorId);
        return aiClient.icebreaker(userService.getById(userId), profileService.findByUserId(userId), mentor,
                userService.getById(mentor.getUserId()));
    }

    /** Student requests a chat; it starts as pending. */
    @Override
    @Transactional
    public CoffeeChat request(CoffeeChatDTO dto) {
        String userId = BaseContext.getCurrentId();
        Mentor mentor = mentorService.getById(dto.getMentorId());
        if (mentor.getUserId().equals(userId)) {
            throw BaseException.badRequest("you cannot request a chat with yourself");
        }
        // TODO(team): enforce maxChatsPerWeek once the rule is agreed.
        CoffeeChat chat = CoffeeChat.builder()
                .id(IdUtil.newId("c"))
                .studentId(userId)
                .mentorId(mentor.getId())
                .status(StatusConstant.CHAT_PENDING)
                .message(dto.getMessage())
                .proposedTimes(dto.getProposedTimes() == null ? List.of() : dto.getProposedTimes())
                .createdAt(Instant.now())
                .build();
        coffeeChatMapper.insert(chat);
        return chat;
    }

    /** Chats where I am the student, or the mentor. */
    @Override
    public List<CoffeeChat> listMine() {
        String userId = BaseContext.getCurrentId();
        Mentor me = mentorMapper.getByUserId(userId);
        return coffeeChatMapper.listByStudentOrMentor(userId, me == null ? null : me.getId());
    }

    /** The mentor accepts or declines; either side can complete an accepted chat (mentor gets points). */
    @Override
    @Transactional
    public CoffeeChat update(String id, CoffeeChatUpdateDTO dto) {
        String userId = BaseContext.getCurrentId();
        CoffeeChat chat = coffeeChatMapper.getById(id);
        if (chat == null) {
            throw BaseException.notFound("coffee chat");
        }
        Mentor mentor = mentorService.getById(chat.getMentorId());
        boolean isMentor = mentor.getUserId().equals(userId);
        boolean isStudent = chat.getStudentId().equals(userId);
        if (!isMentor && !isStudent) {
            throw BaseException.notFound("coffee chat");
        }

        switch (dto.getStatus()) {
            case StatusConstant.CHAT_ACCEPTED, StatusConstant.CHAT_DECLINED -> {
                if (!isMentor) {
                    throw BaseException.forbidden("only the mentor can accept or decline");
                }
                if (!StatusConstant.CHAT_PENDING.equals(chat.getStatus())) {
                    throw BaseException.badRequest("only pending chats can be accepted or declined");
                }
            }
            case StatusConstant.CHAT_COMPLETED -> {
                if (!StatusConstant.CHAT_ACCEPTED.equals(chat.getStatus())) {
                    throw BaseException.badRequest("only accepted chats can be completed");
                }
                mentorMapper.addPoints(mentor.getId(), props.mentors().pointsPerCompletedChat());
            }
            default -> throw BaseException.badRequest("unknown status");
        }
        chat.setStatus(dto.getStatus());
        if (dto.getScheduledAt() != null) {
            chat.setScheduledAt(dto.getScheduledAt().toInstant());
        }
        coffeeChatMapper.update(chat);
        return chat;
    }
}
