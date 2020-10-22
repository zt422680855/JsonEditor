package com.jsoneditor.node;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import com.intellij.ui.treeStructure.Tree;
import com.jsoneditor.Utils;

import javax.swing.tree.TreePath;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/22 22:39
 */
public abstract class TreeNode extends PatchedDefaultMutableTreeNode implements Serializable, Cloneable {

    public String key;

    public boolean filter = false;

    public TreeNode(String key) {
        this.key = key;
    }

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

    public void updateNode() {
        setLabel();
        for (Enumeration<?> e = children(); e.hasMoreElements(); ) {
            TreeNode currNode = (TreeNode) e.nextElement();
            currNode.updateNode();
        }
    }

    public abstract void setLabel();

    @Override
    public TreeNode getParent() {
        return (TreeNode) super.getParent();
    }

    public List<TreeNode> getFullPath() {
        return Arrays.stream(super.getPath()).map(item -> (TreeNode) item).collect(Collectors.toList());
    }

    @Override
    public String getUserObject() {
        return (String) super.getUserObject();
    }

    @Override
    public TreeNode clone() {
        return new ObjectNode();
    }

    public void recursionOption(Consumer<TreeNode> consumer) {
        consumer.accept(this);
        for (Enumeration<?> e = children(); e.hasMoreElements(); ) {
            TreeNode child = (TreeNode) e.nextElement();
            child.recursionOption(consumer);
        }
    }

    public void enpandByNode(Tree tree) {
        tree.expandPath(new TreePath(getPath()));
        TreeNode parent = getParent();
        if (parent != null) {
            parent.enpandByNode(tree);
        }
    }

    public void attachChildrenFromAnotherNode(TreeNode another) {
        for (; another.getChildCount() > 0; ) {
            add((TreeNode) another.getFirstChild());
        }
    }

    public abstract Object getValue();

    public String valueString() {
        return getValue().toString();
    }

}
