package com.jsoneditor;

import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: 19043204
 * @CreateDate: 2020/5/29 23:05
 */
public class Utils {

    private static Pattern integerRegx = Pattern.compile("^[-\\+]?[\\d]*$");

    private static Pattern floatRegx = Pattern.compile("^[-\\+]?[.\\d]*$");

    public static boolean isInteger(String str) {
        return integerRegx.matcher(str).matches();
    }

    public static boolean isFloat(String str) {
        return floatRegx.matcher(str).matches();
    }
}
