package com.jsoneditor;

import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 21:31
 */
public interface Constant {

    enum SelectItem {

        // types
        Object,
        Array,
        String,
        Date,
        Other;

    }

    enum DateFormat {

        // format types
        DEFAULT("Timestamp", Pattern.compile("^[\\d]{11,12}$")),
        ONE("yyyy-MM-dd", Pattern.compile("^[\\d]{4}-[\\d]{2}-[\\d]{2}$")),
        TWO("yyyy/MM/dd", Pattern.compile("^[\\d]{4}/[\\d]{2}/[\\d]{2}$")),
        THREE("yyyy-MM-dd HH:mm:ss", Pattern.compile("^[\\d]{4}-[\\d]{2}-[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2}$")),
        FOUR("yyyy/MM/dd HH:mm:ss", Pattern.compile("^[\\d]{4}/[\\d]{2}/[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2}$")),
        FIVE("yyyy-MM-dd HH:mm:ss SSS", Pattern.compile("^[\\d]{4}-[\\d]{2}-[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2} [\\d]{3}$")),
        SIX("yyyy/MM/dd HH:mm:ss SSS", Pattern.compile("^[\\d]{4}/[\\d]{2}/[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2} [\\d]{3}$")),
        ;

        private String format;

        private Pattern pattern;

        DateFormat(String format, Pattern pattern) {
            this.format = format;
            this.pattern = pattern;
        }

        public String getFormat() {
            return format;
        }

        public Pattern getPattern() {
            return pattern;
        }

    }

    String TEMP = "{\n" +
            "\t\"name\":\"zhengtao\",\n" +
            "\t\"age\":18,\n" +
            "\t\"isHandsome\":true,\n" +
            "\t\"email\":\"hj_zhengt@163.com\",\n" +
            "\t\"timestamp\":1598963565962,\n" +
            "\t\"oneDate\":\"2009-09-01 14:22:41\",\n" +
            "\t\"birthday\":\"1991-10-01\",\n" +
            "\t\"address\":{\n" +
            "\t\t\"country\":\"China\",\n" +
            "\t\t\"province\":\"anhui\",\n" +
            "\t\t\"city\":\"mingguang\"\n" +
            "\t},\n" +
            "\t\"hobbys\":[\n" +
            "\t\t{\n" +
            "\t\t\t\"order\":\"first\",\n" +
            "\t\t\t\"name\":\"coding\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"order\":\"second\",\n" +
            "\t\t\t\"name\":\"fishing\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"order\":\"third\",\n" +
            "\t\t\t\"name\":\"whatever\"\n" +
            "\t\t}\n" +
            "\t]\n" +
            "}";

}
