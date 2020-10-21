package com.jsoneditor.node;

import com.alibaba.fastjson.JSONArray;

import java.util.Enumeration;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:08
 */
public class ArrayNode extends TreeNode {

    public JSONArray value;

    public ArrayNode(String key, JSONArray value) {
        super(key);
        this.value = value;
    }

    @Override
    public void setLabel() {
        int childCount = getChildCount();
        TreeNode parent = getParent();
        if (isRoot() || parent instanceof ObjectNode) {
            setUserObject(key + " : " + "[" + childCount + "]");
        } else if (parent instanceof ArrayNode) {
            setUserObject(parent.getIndex(this) + " : " + "[" + childCount + "]");
        }
    }

    @Override
    public ArrayNode clone() {
        ArrayNode node = new ArrayNode(key, value);
        node.filter = this.filter;
        for (Enumeration<?> e = children(); e.hasMoreElements(); ) {
            TreeNode currNode = (TreeNode) e.nextElement();
            node.add(currNode.clone());
        }
        return node;
    }

    @Override
    public JSONArray getValue() {
        return value;
    }

    @Override
    public String valueString() {
        return value.toString();
    }
}
