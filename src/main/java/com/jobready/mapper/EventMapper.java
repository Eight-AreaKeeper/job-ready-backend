package com.jobready.mapper;

import com.jobready.pojo.dto.EventPageQueryDTO;
import com.jobready.pojo.entity.Event;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventMapper {

    Event getById(String id);

    /** Upcoming events matching the filters, soonest first. */
    List<Event> pageQuery(EventPageQueryDTO query);

    long countQuery(EventPageQueryDTO query);
}
