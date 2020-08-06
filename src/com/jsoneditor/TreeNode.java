package com.jsoneditor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;

import java.io.Serializable;
import java.util.Enumeration;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/22 22:39
 */
public class TreeNode extends PatchedDefaultMutableTreeNode implements Serializable, Cloneable {

    public static Integer OBJECT = 1;
    public static Integer ARRAY = 2;
    public static Integer STRING = 3;
    public static Integer OTHER = 4;

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

    public void loadTreeNodes() {
        if (value instanceof JSONObject) {
            type = TreeNode.OBJECT;
            JSONObject object = (JSONObject) value;
            label = key + " : " + "{" + object.size() + "}";
            object.forEach((k, v) -> {
                TreeNode subNode = new TreeNode(k, v);
                add(subNode);
                subNode.loadTreeNodes();
            });
        } else if (value instanceof JSONArray) {
            type = TreeNode.ARRAY;
            JSONArray array = (JSONArray) value;
            label = key + " : " + "[" + array.size() + "]";
            for (int i = 0; i < array.size(); i++) {
                String k = i + "";
                Object v = array.get(i);
                TreeNode subNode = new TreeNode(k, v);
                add(subNode);
                subNode.loadTreeNodes();
            }
        } else if (value instanceof String) {
            label = key + " : " + value.toString();
            type = TreeNode.STRING;
        } else {
            label = key + " : " + value.toString();
            type = TreeNode.OTHER;
        }
        setUserObject(label);
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
