package com.jsoneditor.node;

import com.alibaba.fastjson.JSONObject;

import java.util.Enumeration;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:08
 */
public final class ObjectNode extends ContainerNode {

    private static final String FORMATTER = "%s : {%d}";

    public JSONObject value;

    public ObjectNode() {
        super("key");
        this.value = new JSONObject(true);
    }

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
    public ObjectNode clone() {
        ObjectNode node = new ObjectNode(key, (JSONObject) value.clone());
        node.filter = this.filter;
        for (Enumeration<?> e = children(); e.hasMoreElements(); ) {
            TreeNode currNode = (TreeNode) e.nextElement();
            node.add(currNode.clone());
        }
        return node;
    }

}
