package com.fcbox.clientb;

public class Utils {

    private Utils() {
        //private constructor
    }

    static boolean isStringBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
}
