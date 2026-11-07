package com.jobready.pojo.vo;

import com.jobready.ai.AiModels.Suggestion;
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
public class ResumeSuggestionVO implements Serializable {

    private List<Suggestion> suggestions;
}
