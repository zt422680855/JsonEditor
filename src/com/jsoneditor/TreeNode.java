package com.jsoneditor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import com.jsoneditor.node.ArrayNode;
import com.jsoneditor.node.ObjectNode;
import com.jsoneditor.node.OtherNode;
import com.jsoneditor.node.StringNode;

import java.io.Serializable;
import java.util.Enumeration;

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

    public Integer type;

    public TreeNode() {
    }

    public TreeNode(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public static TreeNode getNode(String key, Object value) {
        TreeNode node;
        if (value instanceof JSONObject) {
            node = new ObjectNode(key, value);
            // delete
            node.type = 1;
        } else if (value instanceof JSONArray) {
            node = new ArrayNode(key, value);
            node.type = 2;
        } else if (value instanceof String) {
            node = new StringNode(key, value);
            node.type = 3;
        } else {
            node = new OtherNode(key, value);
            node.type = 4;
        }
        return node;
    }

    public void updateNode() {
        TreeNode parent = (TreeNode) getParent();
        if (parent != null && ARRAY.equals(parent.type)) {
            updateArrayNode();
            parent.updateArrayNodeChildren();
        } else {
            if (value instanceof JSONObject || OBJECT.equals(type)) {
                label = key + " : " + "{" + getChildCount() + "}";
            } else if (value instanceof JSONArray || ARRAY.equals(type)) {
                label = key + " : " + "[" + getChildCount() + "]";
            } else {
                label = key + " : " + (value != null ? value.toString() : "");
            }
            setUserObject(label);
        }
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
        for (Enumeration<?> e = children(); e.hasMoreElements(); ) {
            TreeNode currNode = (TreeNode) e.nextElement();
            currNode.updateArrayNode();
        }
    }

    public void updateObjectNodeChildren() {
        for (Enumeration<?> e = children(); e.hasMoreElements(); ) {
            TreeNode currNode = (TreeNode) e.nextElement();
            currNode.updateNode();
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
