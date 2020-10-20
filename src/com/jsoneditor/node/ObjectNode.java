package com.jsoneditor.node;

import com.alibaba.fastjson.JSONObject;

import java.util.Enumeration;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:08
 */
public class ObjectNode extends TreeNode {

    public JSONObject value;

    public ObjectNode() {

    }

    public ObjectNode(String key, JSONObject value) {
        super(key);
        this.value = value;
        updateNode();
    }

    @Override
    public void setLabel() {
        int childCount = getChildCount();
        TreeNode parent = getParent();
        if (parent == null || parent instanceof ObjectNode) {
            label = key + " : " + "{" + childCount + "}";
        } else if (parent instanceof ArrayNode) {
            label = parent.getIndex(this) + " : " + "{" + childCount + "}";
        }
        setUserObject(this.label);
    }

    @Override
    public ObjectNode clone() {
        ObjectNode node = new ObjectNode(key, value);
        node.filter = this.filter;
        for (Enumeration<?> e = children(); e.hasMoreElements(); ) {
            TreeNode currNode = (TreeNode) e.nextElement();
            node.add(currNode.clone());
        }
        return node;
    }

    @Override
    public JSONObject getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
