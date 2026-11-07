package com.jobready.common.result;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** List payload: { "items": [...], "total": n }. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListResult<T> implements Serializable {

    private List<T> items;
    private long total;

    public static <T> ListResult<T> of(List<T> items) {
        return new ListResult<>(items, items.size());
    }
}
