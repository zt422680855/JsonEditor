package com.jsoneditor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.ui.treeStructure.Tree;
import com.jsoneditor.node.*;

import javax.swing.tree.TreePath;
import java.util.Enumeration;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 22:32
 */
public class TreeUtils {

    public static TreeNode getNode(String key, Object value) {
        TreeNode node;
        if (value == null) {
            node = new NullNode(key);
        } else if (value instanceof JSONObject) {
            node = new ObjectNode(key, (JSONObject) value);
        } else if (value instanceof JSONArray) {
            node = new ArrayNode(key, (JSONArray) value);
        } else if (value instanceof String) {
            if (Utils.canConvertToDate(value)) {
                node = new DateNode(key, (String) value);
            } else {
                node = new StringNode(key, (String) value);
            }
        } else {
            if (Utils.canConvertToDate(value)) {
                node = new DateNode(key, (Long) value);
            } else {
                node = new OtherNode(key, value);
            }
        }
        return node;
    }

    public static void refreshJson(TreeNode node) {
        if (node instanceof ObjectNode) {
            JSONObject obj = (JSONObject) node.getValue();
            obj.clear();
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode currNode = (TreeNode) e.nextElement();
                obj.put(currNode.key, currNode.getValue());
                refreshJson(currNode);
            }
        } else if (node instanceof ArrayNode) {
            JSONArray array = (JSONArray) node.getValue();
            array.clear();
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode currNode = (TreeNode) e.nextElement();
                array.add(currNode.getValue());
                refreshJson(currNode);
            }
        }
    }

    public static void refreshTree(TreeNode node) {
        node.removeAllChildren();
        Object value = node.getValue();
        if (value instanceof JSONObject) {
            JSONObject object = (JSONObject) value;
            object.forEach((k, v) -> {
                TreeNode subNode = getNode(k, v);
                node.add(subNode);
                refreshTree(subNode);
            });
        } else if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            for (int i = 0; i < array.size(); i++) {
                String k = String.valueOf(i);
                Object v = array.get(i);
                TreeNode subNode = getNode(k, v);
                node.add(subNode);
                refreshTree(subNode);
            }
        }
        if (node.isRoot()) {
            node.updateNode();
        }
    }

    public static void expandTree(Tree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
            TreeNode n = (TreeNode) e.nextElement();
            TreePath path = parent.pathByAddingChild(n);
            expandTree(tree, path);
        }
        tree.expandPath(parent);
    }

    public static void collapseTree(Tree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
            TreeNode n = (TreeNode) e.nextElement();
            TreePath path = parent.pathByAddingChild(n);
            collapseTree(tree, path);
        }
        tree.collapsePath(parent);
    }
}
