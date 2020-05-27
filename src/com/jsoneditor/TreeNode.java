package com.jsoneditor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;

import java.awt.datatransfer.DataFlavor;
import java.io.Serializable;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/22 22:39
 */
public class TreeNode extends PatchedDefaultMutableTreeNode implements Serializable, Cloneable {

    static final Integer OBJECT = 1;
    static final Integer ARRAY = 2;
    static final Integer STRING = 3;
    static final Integer OTHER = 4;

    public String key;

    public Object value;

    public String label;

    public Integer type = STRING;

    public TreeNode() {
    }

    public TreeNode(String key, Object value) {
        if (value instanceof JSONObject) {
            JSONObject object = (JSONObject) value;
            label = key + " : " + "{" + object.size() + "}";
        } else if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            label = key + " : " + "[" + array.size() + "]";
        } else {
            label = key + " : " + (value != null ? value.toString() : "");
        }
        setUserObject(label);
        this.key = key;
        this.value = value;
    }

    public void loadTreeNodes() {
        if (value instanceof JSONObject) {
            type = TreeNode.OBJECT;
            JSONObject jsonObject = (JSONObject) value;
            jsonObject.forEach((k, v) -> {
                TreeNode subNode = new TreeNode(k, v);
                add(subNode);
                subNode.loadTreeNodes();
            });
        } else if (value instanceof JSONArray) {
            type = TreeNode.ARRAY;
            JSONArray array = (JSONArray) value;
            for (int i = 0; i < array.size(); i++) {
                String k = i + "";
                Object v = array.get(i);
                TreeNode subNode = new TreeNode(k, v);
                add(subNode);
                subNode.loadTreeNodes();
            }
        } else if (value instanceof String) {
            type = TreeNode.STRING;
        } else {
            type = TreeNode.OTHER;
        }
    }

    public void updateNode() {
        if (value instanceof JSONObject || OBJECT.equals(type)) {
            label = key + " : " + "{" + getChildCount() + "}";
        } else if (value instanceof JSONArray || ARRAY.equals(type)) {
            label = key + " : " + "[" + getChildCount() + "]";
        } else {
            label = key + " : " + (value != null ? value.toString() : "");
        }
        setUserObject(label);
    }

    public void updateArrayNode() {
        if (OBJECT.equals(type)) {
            label = getParent().getIndex(this) + " : " + "{" + getChildCount() + "}";
        } else if (ARRAY.equals(type)) {
            label = getParent().getIndex(this) + " : " + "[" + getChildCount() + "]";
        } else {
            label = getParent().getIndex(this) + " : " + (value != null ? value.toString() : "");
        }
        setUserObject(label);
    }

    public void updateArrayNodeChildren() {
        for (int i = 0; i < getChildCount(); i++) {
            ((TreeNode) getChildAt(i)).updateArrayNode();
        }
    }

    public void updateObjectNodeChildren() {
        for (int i = 0; i < getChildCount(); i++) {
            ((TreeNode) getChildAt(i)).updateNode();
        }
    }

    @Override
    public TreeNode clone() {
        TreeNode node = new TreeNode();
        node.key = key;
        node.value = value;
        node.type = type;
        node.label = label;
        node.setUserObject(node.label);
        node.setAllowsChildren(getAllowsChildren());
        for (int i = 0; i < getChildCount(); i++) {
            node.add(((TreeNode) getChildAt(i)).clone());
        }
        return node;
    }

}
