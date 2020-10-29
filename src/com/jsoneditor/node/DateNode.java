package com.jsoneditor.node;

import com.jsoneditor.Constant.DateFormat;
import com.jsoneditor.Utils;
import icons.Icons;

import javax.swing.*;
import java.util.Date;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/16 22:04
 */
public final class DateNode extends LeafNode {

    // 1 显示为时间戳，2 显示为字符串，默认显示为时间戳
    public String format;

    public Date value;

    public DateNode(String key, String value) {
        super(key);
        DateFormat format = Utils.getFormat(value);
        if (format != null) {
            this.format = format.getFormat();
            Date d = Utils.strToDate(value, this.format);
            this.value = d != null ? d : new Date();
        } else {
            this.format = DateFormat.DEFAULT.getFormat();
            this.value = new Date();
        }
    }

    public DateNode(String key, Long value) {
        this(key, new Date(value));
    }

    public DateNode(String key, Date date) {
        this(key, date, DateFormat.DEFAULT.getFormat());
    }

    public DateNode(String key, Date date, String dateFormat) {
        super(key);
        this.format = dateFormat;
        this.value = date;
    }

    @Override
    public Object getValue() {
        if (DateFormat.DEFAULT.getFormat().equals(format)) {
            return value.getTime();
        } else {
            return Utils.dateToStr(value, format);
        }
    }

    @Override
    public DateNode getCloneNode() {
        return new DateNode(key, value, format);
    }

    @Override
    public Icon icon() {
        return Icons.DATE;
    }

}
