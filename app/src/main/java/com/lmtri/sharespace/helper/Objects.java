package com.lmtri.sharespace.helper;

/**
 * Created by lmtri on 8/12/2017.
 */

public class Objects {
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null)
            throw new NullPointerException(message);
        return obj;
    }

    public static boolean nonNull(Object obj) {
        return obj != null;
    }
}
