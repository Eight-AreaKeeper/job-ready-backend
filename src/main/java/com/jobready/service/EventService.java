package com.jobready.service;

import com.jobready.common.result.ListResult;
import com.jobready.pojo.dto.EventPageQueryDTO;
import com.jobready.pojo.entity.Event;

public interface EventService {

    ListResult<Event> pageQuery(EventPageQueryDTO query);

    Event getById(String id);
}
