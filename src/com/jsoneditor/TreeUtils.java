package com.jsoneditor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.ui.treeStructure.Tree;
import com.jsoneditor.node.ArrayNode;
import com.jsoneditor.node.ObjectNode;
import com.jsoneditor.node.OtherNode;
import com.jsoneditor.node.StringNode;

import javax.swing.tree.TreePath;
import java.util.Enumeration;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 22:32
 */
public class TreeUtils {

    public static void refreshJson(TreeNode node) {
        if (node instanceof ObjectNode) {
            JSONObject obj = (JSONObject) node.value;
            obj.clear();
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode currNode = (TreeNode) e.nextElement();
                if ("null".equals(currNode.value)) {
                    if (currNode instanceof OtherNode) {
                        currNode.value = null;
                    }
                }
                obj.put(currNode.key, currNode.value);
                refreshJson(currNode);
            }
        } else if (node instanceof ArrayNode) {
            JSONArray array = (JSONArray) node.value;
            array.clear();
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode currNode = (TreeNode) e.nextElement();
                if ("null".equals(currNode.value)) {
                    currNode.value = null;
                }
                array.add(currNode.value);
                refreshJson(currNode);
            }
        }
    }

    public static void refreshTree(TreeNode node) {
        node.removeAllChildren();
        if (node.value instanceof JSONObject) {
            JSONObject object = (JSONObject) node.value;
            node.label = node.key + " : " + "{" + object.size() + "}";
            object.forEach((k, v) -> {
                TreeNode subNode = TreeNode.getNode(k, v);
                node.add(subNode);
                refreshTree(subNode);
            });
        } else if (node.value instanceof JSONArray) {
            JSONArray array = (JSONArray) node.value;
            node.label = node.key + " : " + "[" + array.size() + "]";
            for (int i = 0; i < array.size(); i++) {
                String k = i + "";
                Object v = array.get(i);
                TreeNode subNode = TreeNode.getNode(k, v);
                node.add(subNode);
                refreshTree(subNode);
            }
        } else {
            if (node.value == null) {
                node.value = "null";
            }
            node.label = node.key + " : " + node.value.toString();
        }
        node.setUserObject(node.label);
    }

    public static void expandTree(Tree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandTree(tree, path);
            }
        }
        tree.expandPath(parent);
    }

    public static void collapseTree(Tree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                collapseTree(tree, path);
            }
        }
        tree.collapsePath(parent);
    }
}
