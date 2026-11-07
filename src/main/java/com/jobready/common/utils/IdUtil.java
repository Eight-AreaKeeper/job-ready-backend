package com.jobready.common.utils;

import java.util.UUID;

/** String ids with a readable prefix: u_ user, p_ profile, o_ opportunity, s_, r_, m_, c_, i_. */
public final class IdUtil {

    private IdUtil() {
    }

    public static String newId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
