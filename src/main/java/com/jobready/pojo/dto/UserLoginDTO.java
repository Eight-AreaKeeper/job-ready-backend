package com.jobready.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Data;

/** POST /auth/login */
@Data
public class UserLoginDTO implements Serializable {

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
}
