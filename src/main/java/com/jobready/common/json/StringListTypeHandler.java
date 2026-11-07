package com.jobready.common.json;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

/** JSON column <-> List<String>. */
public class StringListTypeHandler extends JsonTypeHandler<List<String>> {

    public StringListTypeHandler() {
        super(new TypeReference<>() {});
    }
}
