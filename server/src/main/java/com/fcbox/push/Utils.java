package com.fcbox.push;

/**
 * Created by codezjx on 2019/7/14.<br/>
 */
final class Utils {
    
    private static final String TAG = "Utils";
    
    private Utils() {
        // private constructor
    }

    static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    static boolean isStringBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
    
}
