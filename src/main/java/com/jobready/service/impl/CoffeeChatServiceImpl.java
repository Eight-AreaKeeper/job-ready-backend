package com.jobready.service.impl;

import com.jobready.ai.AiClient;
import com.jobready.common.constant.StatusConstant;
import com.jobready.common.context.BaseContext;
import com.jobready.common.exception.BaseException;
import com.jobready.common.properties.AppProperties;
import com.jobready.common.utils.IdUtil;
import com.jobready.mapper.CoffeeChatMapper;
import com.jobready.mapper.MentorMapper;
import com.jobready.mapper.UserMapper;
import com.jobready.pojo.dto.CoffeeChatDTO;
import com.jobready.pojo.dto.CoffeeChatUpdateDTO;
import com.jobready.pojo.entity.CoffeeChat;
import com.jobready.pojo.entity.Mentor;
import com.jobready.pojo.entity.User;
import com.jobready.pojo.vo.CoffeeChatVO;
import com.jobready.service.CoffeeChatService;
import com.jobready.service.MentorService;
import com.jobready.service.ProfileService;
import com.jobready.service.UserService;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoffeeChatServiceImpl implements CoffeeChatService {

    private final CoffeeChatMapper coffeeChatMapper;
    private final MentorMapper mentorMapper;
    private final UserMapper userMapper;
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
    public CoffeeChatVO request(CoffeeChatDTO dto) {
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
        return CoffeeChatVO.from(chat, userService.getById(userId).getName(),
                userService.getById(mentor.getUserId()).getName());
    }

    /** Chats where I am the student, or the mentor. */
    @Override
    public List<CoffeeChatVO> listMine() {
        String userId = BaseContext.getCurrentId();
        Mentor me = mentorMapper.getByUserId(userId);
        List<CoffeeChat> chats = coffeeChatMapper.listByStudentOrMentor(userId, me == null ? null : me.getId());
        if (chats.isEmpty()) {
            return List.of();
        }
        // mentorId -> the mentor's users.id, then one user lookup for every name we need.
        Map<String, String> mentorUserIds = new HashMap<>();
        for (CoffeeChat c : chats) {
            mentorUserIds.computeIfAbsent(c.getMentorId(), id -> {
                Mentor m = mentorMapper.getById(id);
                return m == null ? null : m.getUserId();
            });
        }
        List<String> userIds = chats.stream().map(CoffeeChat::getStudentId).collect(Collectors.toList());
        mentorUserIds.values().stream().filter(Objects::nonNull).forEach(userIds::add);
        Map<String, String> names = userMapper.listByIds(userIds.stream().distinct().toList()).stream()
                .collect(Collectors.toMap(User::getId, User::getName));
        return chats.stream()
                .map(c -> CoffeeChatVO.from(c, names.get(c.getStudentId()),
                        names.get(mentorUserIds.get(c.getMentorId()))))
                .toList();
    }

    /** The mentor accepts or declines; either side can complete an accepted chat (mentor gets points). */
    @Override
    @Transactional
    public CoffeeChatVO update(String id, CoffeeChatUpdateDTO dto) {
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
        return CoffeeChatVO.from(chat, userService.getById(chat.getStudentId()).getName(),
                userService.getById(mentor.getUserId()).getName());
    }
}
