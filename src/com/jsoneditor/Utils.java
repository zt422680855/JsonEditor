package com.jsoneditor;

import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/29 23:05
 */
public class Utils {

    private final static Pattern INTEGER_REGX = Pattern.compile("^[-\\+]?[\\d]*$");

    private final static Pattern FLOAT_REGX = Pattern.compile("^[-\\+]?[.\\d]*$");

    public static boolean isInteger(String str) {
        return INTEGER_REGX.matcher(str).matches();
    }

    public static boolean isFloat(String str) {
        return FLOAT_REGX.matcher(str).matches();
    }
}
