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
public abstract class TreeNode extends PatchedDefaultMutableTreeNode implements Serializable, Cloneable {

    public String key;

    public Object value;

    public String label;

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
        } else if (value instanceof JSONArray) {
            node = new ArrayNode(key, value);
        } else if (value instanceof String) {
            node = new StringNode(key, value);
        } else {
            node = new OtherNode(key, value);
        }
        return node;
    }

    public void updateNode() {
        setUserObject(label);
        for (Enumeration<?> e = children(); e.hasMoreElements(); ) {
            TreeNode currNode = (TreeNode) e.nextElement();
            currNode.updateNode();
        }
    }

    @Override
    public TreeNode getParent() {
        return (TreeNode) super.getParent();
    }

    @Override
    public TreeNode clone() {
        return new ObjectNode();
    }

    public void attachChildrenFromAnotherNode(TreeNode another) {
        for (; another.getChildCount() > 0; ) {
            add((TreeNode) another.getFirstChild());
        }
    }

}
