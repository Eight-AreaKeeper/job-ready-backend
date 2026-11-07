package com.jobready.service.impl;

import com.jobready.common.constant.StatusConstant;
import com.jobready.common.context.BaseContext;
import com.jobready.common.exception.BaseException;
import com.jobready.common.utils.IdUtil;
import com.jobready.mapper.MentorMapper;
import com.jobready.mapper.UserMapper;
import com.jobready.pojo.dto.MentorDTO;
import com.jobready.pojo.entity.Mentor;
import com.jobready.pojo.entity.User;
import com.jobready.pojo.vo.MentorVO;
import com.jobready.service.MentorService;
import com.jobready.service.UserService;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentorServiceImpl implements MentorService {

    private final MentorMapper mentorMapper;
    private final UserMapper userMapper;
    private final UserService userService;

    /** Creates or updates the caller's mentor card. Account role must be mentor. */
    @Override
    @Transactional
    public MentorVO signUp(MentorDTO dto) {
        User user = userService.getById(BaseContext.getCurrentId());
        if (!StatusConstant.ROLE_MENTOR.equals(user.getRole())) {
            throw BaseException.forbidden("only accounts with role mentor can sign up as a mentor");
        }
        Mentor existing = mentorMapper.getByUserId(user.getId());
        Mentor mentor = Mentor.builder()
                .id(existing == null ? IdUtil.newId("m") : existing.getId())
                .userId(user.getId())
                .headline(dto.getHeadline())
                .company(dto.getCompany())
                .industries(dto.getIndustries() == null ? List.of() : dto.getIndustries())
                .expertise(dto.getExpertise() == null ? List.of() : dto.getExpertise())
                .availability(dto.getAvailability() == null ? List.of() : dto.getAvailability())
                .maxChatsPerWeek(dto.getMaxChatsPerWeek() == null ? 2 : dto.getMaxChatsPerWeek())
                .points(existing == null ? 0 : existing.getPoints())
                .build();
        if (existing == null) {
            mentorMapper.insert(mentor);
        } else {
            mentorMapper.update(mentor);
        }
        return MentorVO.from(mentor, user.getName());
    }

    /** Filters in memory, which is fine at hackathon scale. */
    @Override
    public List<MentorVO> list(String industry, String q) {
        List<Mentor> all = mentorMapper.list();
        if (all.isEmpty()) {
            return List.of();
        }
        Map<String, String> names = userMapper.listByIds(all.stream().map(Mentor::getUserId).toList()).stream()
                .collect(Collectors.toMap(User::getId, User::getName));
        String needle = q == null ? null : q.toLowerCase(Locale.ROOT);
        return all.stream()
                .filter(m -> industry == null || (m.getIndustries() != null
                        && m.getIndustries().stream().anyMatch(i -> i.equalsIgnoreCase(industry))))
                .map(m -> MentorVO.from(m, names.get(m.getUserId())))
                .filter(v -> needle == null || Stream.concat(
                                Stream.of(v.getName(), v.getHeadline(), v.getCompany()),
                                v.getExpertise() == null ? Stream.empty() : v.getExpertise().stream())
                        .filter(Objects::nonNull)
                        .anyMatch(s -> s.toLowerCase(Locale.ROOT).contains(needle)))
                .toList();
    }

    @Override
    public Mentor getById(String id) {
        Mentor m = mentorMapper.getById(id);
        if (m == null) {
            throw BaseException.notFound("mentor");
        }
        return m;
    }
}
