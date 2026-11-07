package com.jobready.service.impl;

import com.jobready.common.exception.BaseException;
import com.jobready.common.utils.IdUtil;
import com.jobready.common.utils.JwtUtil;
import com.jobready.mapper.UserMapper;
import com.jobready.pojo.dto.UserLoginDTO;
import com.jobready.pojo.dto.UserRegisterDTO;
import com.jobready.pojo.entity.User;
import com.jobready.pojo.vo.UserLoginVO;
import com.jobready.pojo.vo.UserVO;
import com.jobready.service.UserService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserLoginVO register(UserRegisterDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();
        if (userMapper.getByEmail(email) != null) {
            throw BaseException.conflict("email is already registered");
        }
        User user = User.builder()
                .id(IdUtil.newId("u"))
                .name(dto.getName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .createdAt(Instant.now())
                .build();
        userMapper.insert(user);
        return new UserLoginVO(UserVO.from(user), jwtUtil.createToken(user.getId()));
    }

    @Override
    public UserLoginVO login(UserLoginDTO dto) {
        User user = userMapper.getByEmail(dto.getEmail().trim().toLowerCase());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw BaseException.unauthorized("wrong email or password");
        }
        return new UserLoginVO(UserVO.from(user), jwtUtil.createToken(user.getId()));
    }

    @Override
    public User getById(String id) {
        User user = userMapper.getById(id);
        if (user == null) {
            throw BaseException.notFound("user");
        }
        return user;
    }
}
