package com.jobready.service;

import com.jobready.pojo.dto.MentorDTO;
import com.jobready.pojo.entity.Mentor;
import com.jobready.pojo.vo.MentorVO;
import java.util.List;

public interface MentorService {

    MentorVO signUp(MentorDTO dto);

    List<MentorVO> list(String industry, String q);

    /** 404 if it doesn't exist. */
    Mentor getById(String id);
}
