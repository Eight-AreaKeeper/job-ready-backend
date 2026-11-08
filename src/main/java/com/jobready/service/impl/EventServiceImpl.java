package com.jobready.service.impl;

import com.jobready.common.exception.BaseException;
import com.jobready.common.result.ListResult;
import com.jobready.mapper.EventMapper;
import com.jobready.pojo.dto.EventPageQueryDTO;
import com.jobready.pojo.entity.Event;
import com.jobready.service.EventService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;

    /** Upcoming events (not ended yet), soonest first. */
    @Override
    public ListResult<Event> pageQuery(EventPageQueryDTO query) {
        query.setNow(Instant.now());
        return new ListResult<>(eventMapper.pageQuery(query), eventMapper.countQuery(query));
    }

    @Override
    public Event getById(String id) {
        Event e = eventMapper.getById(id);
        if (e == null) {
            throw BaseException.notFound("event");
        }
        return e;
    }
}
