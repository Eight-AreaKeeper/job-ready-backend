package com.jobready.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.Data;

/** POST /auth/register */
@Data
public class UserRegisterDTO implements Serializable {

    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 8, message = "must be at least 8 characters")
    private String password;
    @NotBlank
    @Pattern(regexp = "student|mentor", message = "must be student or mentor")
    private String role;
}
