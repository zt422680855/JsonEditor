package com.jsoneditor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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

    /**
     * 是否可以转为Date类型
     *
     * @param value
     * @return
     */
    public static boolean canConvertToDate(Object value) {
        boolean res = false;
        if (value instanceof Long) {
            Long v = (Long) value;
            res = v > Integer.MAX_VALUE;
        } else if (value instanceof String) {
            String v = (String) value;
            res = getFormat(v) != null;
        }
        return res;
    }

    public static Constant.DateFormat getFormat(String dateStr) {
        return Arrays.stream(Constant.DateFormat.values()).filter(f -> !Constant.DateFormat.DEFAULT.equals(f))
                .filter(f -> f.getPattern().matcher(dateStr).matches()).findFirst().orElse(null);
    }

    public static Date strToDate(String str, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        try {
            return format.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String dateToStr(Date date, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(date);
    }

}
