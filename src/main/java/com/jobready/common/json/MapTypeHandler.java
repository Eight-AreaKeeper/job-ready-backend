package com.jobready.common.json;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;

/** JSON column <-> Map<String, Object>. */
public class MapTypeHandler extends JsonTypeHandler<Map<String, Object>> {

    public MapTypeHandler() {
        super(new TypeReference<>() {});
    }
}
