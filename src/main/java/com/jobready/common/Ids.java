package com.jobready.common;

import java.util.UUID;

/** String ids with a readable prefix, e.g. u_1a2b3c4d5e6f (users), o_... (opportunities). */
public final class Ids {

    private Ids() {
    }

    public static String newId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
