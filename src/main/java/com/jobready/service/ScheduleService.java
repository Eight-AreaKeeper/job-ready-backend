package com.jobready.service;

import com.jobready.pojo.vo.ScheduleItemVO;
import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {

    List<ScheduleItemVO> list(LocalDate from, LocalDate to);

    ScheduleItemVO add(String opportunityId);

    ScheduleItemVO updateStatus(String id, String status);

    void delete(String id);
}
