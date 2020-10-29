package com.jsoneditor.node;

import com.alibaba.fastjson.JSONArray;
import icons.Icons;

import javax.swing.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:08
 */
public final class ArrayNode extends ContainerNode {

    private static final String FORMATTER = "%s : [%d]";

    public JSONArray value;

    public ArrayNode(String key, JSONArray value) {
        super(key);
        this.value = value;
    }

    @Override
    public String getFormatter() {
        return FORMATTER;
    }

    @Override
    public JSONArray getValue() {
        return value;
    }

    @Override
    public ArrayNode getCloneNode() {
        return new ArrayNode(key, (JSONArray) value.clone());
    }

    @Override
    public Icon icon() {
        return Icons.ARRAY;
    }

}
