package com.jobready.users;

import com.jobready.common.ApiException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public User get(String userId) {
        return users.findById(userId).orElseThrow(() -> ApiException.notFound("user"));
    }
}
