package com.jobready.pojo.vo;

import com.jobready.pojo.entity.User;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO implements Serializable {

    private String id;
    private String name;
    private String email;
    private String role;

    public static UserVO from(User u) {
        return new UserVO(u.getId(), u.getName(), u.getEmail(), u.getRole());
    }
}
