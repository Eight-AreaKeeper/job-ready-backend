package com.jobready.pojo.vo;

import com.jobready.pojo.entity.Resume;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeTailorVO implements Serializable {

    private Resume resume;
    private List<String> changes;
}
