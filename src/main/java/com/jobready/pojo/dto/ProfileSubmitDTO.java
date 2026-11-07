package com.jobready.pojo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/** POST /profile */
@Data
public class ProfileSubmitDTO implements Serializable {

    @NotNull
    @Valid
    private List<ProfileAnswerDTO> answers;
    @Size(max = 3, message = "can have at most 3 entries")
    private List<String> industries;
    private String location;
}
