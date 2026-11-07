package com.jobready.common.json;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;

/** JSON column <-> List<Map<String, Object>>. */
public class MapListTypeHandler extends JsonTypeHandler<List<Map<String, Object>>> {

    public MapListTypeHandler() {
        super(new TypeReference<>() {});
    }
}
