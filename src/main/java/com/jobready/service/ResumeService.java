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

    ResumeTailorVO tailor(String id, ResumeTailorDTO dto);

    ResumeSuggestionVO suggestions(String id, String opportunityId);
}
