package com.jobready.common.context;

/** Holds the logged-in user's id for the current request (set by JwtTokenInterceptor). */
public class BaseContext {

    private static final ThreadLocal<String> CURRENT_ID = new ThreadLocal<>();

    public static void setCurrentId(String id) {
        CURRENT_ID.set(id);
    }

    public static String getCurrentId() {
        return CURRENT_ID.get();
    }

    public static void removeCurrentId() {
        CURRENT_ID.remove();
    }
}
