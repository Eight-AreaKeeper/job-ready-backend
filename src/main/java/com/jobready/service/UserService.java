package com.jobready.service;

import com.jobready.pojo.dto.UserLoginDTO;
import com.jobready.pojo.dto.UserRegisterDTO;
import com.jobready.pojo.entity.User;
import com.jobready.pojo.vo.UserLoginVO;

public interface UserService {

    UserLoginVO register(UserRegisterDTO dto);

    UserLoginVO login(UserLoginDTO dto);

    /** 404 if the user doesn't exist. */
    User getById(String id);
}
