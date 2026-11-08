package com.jobready.service;

import com.jobready.pojo.dto.ResumeTailorDTO;
import com.jobready.pojo.entity.Resume;
import com.jobready.pojo.vo.ResumeSuggestionVO;
import com.jobready.pojo.vo.ResumeTailorVO;
import java.util.List;

public interface ResumeService {

    List<Resume> list();

    Resume getById(String id);

    Resume generate();

    ResumeTailorVO tailor(ResumeTailorDTO dto);

    /** PDF bytes of one of the current user's resumes. */
    byte[] pdf(String id);

    ResumeSuggestionVO suggestions(String id, String opportunityId);
}
