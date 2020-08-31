package com.jsoneditor;

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
        Other;

    }

    String TEMP = "{\n" +
            "\t\"name\":\"zhengt\",\n" +
            "\t\"age\":18,\n" +
            "\t\"isHandsome\":true,\n" +
            "\t\"email\":\"hj_zhengt@163.com\",\n" +
            "\t\"address\":{\n" +
            "\t\t\"country\":\"China\",\n" +
            "\t\t\"province\":\"jiangsu\",\n" +
            "\t\t\"city\":\"nanjing\"\n" +
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
