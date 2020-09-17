package com.jsoneditor.node;

import com.jsoneditor.Constant.DateFormat;
import com.jsoneditor.Utils;

import java.util.Date;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/16 22:04
 */
public class DateNode extends TreeNode {

    // 1 显示为时间戳，2 显示为字符串，默认显示为时间戳
    public String format;

    public Date date;

    public DateNode(String key, Object value) {
        super(key, value);
        if (value instanceof Long) {
            this.format = DateFormat.DEFAULT.getFormat();
            this.date = new Date((Long) value);
        } else if (value instanceof String) {
            String v = (String) value;
            DateFormat format = Utils.getFormat(v);
            if (format != null) {
                this.format = format.getFormat();
                Date d = Utils.strToDate(v, this.format);
                this.date = d != null ? d : new Date();
            } else {
                this.format = DateFormat.DEFAULT.getFormat();
                this.date = new Date();
            }
        }
        updateNode();
    }

    public DateNode(String key, Long value) {
        super(key, value);
        this.format = DateFormat.DEFAULT.getFormat();
        this.date = new Date(value);
        updateNode();
    }

    public DateNode(String key, Date date, String dateFormat) {
        super(key, null);
        value = Utils.dateToStr(date, dateFormat);
        this.format = dateFormat;
        this.date = date;
        updateNode();
    }

    @Override
    public void updateNode() {
        TreeNode parent = getParent();
        if (parent == null || parent instanceof ObjectNode) {
            label = key + " : " + value.toString();
        } else if (parent instanceof ArrayNode) {
            label = parent.getIndex(this) + " : " + value.toString();
        }
        super.updateNode();
    }

    @Override
    public DateNode clone() {
        DateNode node = new DateNode(key, value);
        node.filter = filter;
        return node;
    }
}
