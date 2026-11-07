package com.jobready.pojo.vo;

import com.jobready.pojo.entity.AvailabilitySlot;
import com.jobready.pojo.entity.Mentor;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Mentor card: signup fields plus id, userId, name and points. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorVO implements Serializable {

    private String id;
    private String userId;
    private String name;
    private String headline;
    private String company;
    private List<String> industries;
    private List<String> expertise;
    private List<AvailabilitySlot> availability;
    private Integer maxChatsPerWeek;
    private Integer points;

    public static MentorVO from(Mentor m, String name) {
        return new MentorVO(m.getId(), m.getUserId(), name, m.getHeadline(), m.getCompany(), m.getIndustries(),
                m.getExpertise(), m.getAvailability(), m.getMaxChatsPerWeek(), m.getPoints());
    }
}
