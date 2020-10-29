package com.jsoneditor.node;

import com.alibaba.fastjson.JSONObject;
import icons.Icons;

import javax.swing.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:08
 */
public final class ObjectNode extends ContainerNode {

    private static final String FORMATTER = "%s : {%d}";

    public JSONObject value;

    public ObjectNode(String key, JSONObject value) {
        super(key);
        this.value = value;
    }

    @Override
    public String getFormatter() {
        return FORMATTER;
    }

    @Override
    public JSONObject getValue() {
        return value;
    }

    @Override
    public ObjectNode getCloneNode() {
        return new ObjectNode(key, (JSONObject) value.clone());
    }

    @Override
    public Icon icon() {
        return Icons.OBJECT;
    }

}
