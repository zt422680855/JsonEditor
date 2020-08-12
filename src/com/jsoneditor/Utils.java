package com.jsoneditor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsoneditor.node.ArrayNode;
import com.jsoneditor.node.ObjectNode;

import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/29 23:05
 */
public class Utils {

    private final static Pattern INTEGER_REGX = Pattern.compile("^[-\\+]?[\\d]*$");

    private final static Pattern FLOAT_REGX = Pattern.compile("^[-\\+]?[.\\d]*$");

    public static boolean isInteger(String str) {
        return INTEGER_REGX.matcher(str).matches();
    }

    public static boolean isFloat(String str) {
        return FLOAT_REGX.matcher(str).matches();
    }

    public static void refreshJson(TreeNode node) {
        if (node instanceof ObjectNode) {
            JSONObject obj = (JSONObject) node.value;
            obj.clear();
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode currNode = (TreeNode) e.nextElement();
                obj.put(currNode.key, currNode.value);
                refreshJson(currNode);
            }
        } else if (node instanceof ArrayNode) {
            JSONArray array = (JSONArray) node.value;
            array.clear();
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode currNode = (TreeNode) e.nextElement();
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
            node.label = node.key + " : " + node.value.toString();
        }
        node.setUserObject(node.label);
    }
}
