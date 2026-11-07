package com.jobready.common.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jobready.pojo.entity.AvailabilitySlot;
import java.util.List;

/** JSON column <-> List<AvailabilitySlot>. */
public class SlotListTypeHandler extends JsonTypeHandler<List<AvailabilitySlot>> {

    public SlotListTypeHandler() {
        super(new TypeReference<>() {});
    }
}
