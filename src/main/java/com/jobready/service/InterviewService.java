package com.jobready.service;

import com.jobready.pojo.dto.InterviewAnswerDTO;
import com.jobready.pojo.dto.InterviewStartDTO;
import com.jobready.pojo.vo.InterviewFeedbackVO;
import com.jobready.pojo.vo.InterviewVO;

public interface InterviewService {

    InterviewVO start(InterviewStartDTO dto);

    InterviewFeedbackVO answer(String id, InterviewAnswerDTO dto);
}
