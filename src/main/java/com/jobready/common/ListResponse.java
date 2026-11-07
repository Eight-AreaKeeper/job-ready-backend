package com.jobready.common;

import java.util.List;

/** List body from the API docs: {@code { "items": [...], "total": n }}. */
public record ListResponse<T>(List<T> items, long total) {

    public static <T> ListResponse<T> of(List<T> items) {
        return new ListResponse<>(items, items.size());
    }

    public static <T> ListResponse<T> of(List<T> items, long total) {
        return new ListResponse<>(items, total);
    }
}
