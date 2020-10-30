package com.jsoneditor.node;

import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import com.intellij.ui.treeStructure.Tree;
import icons.Icons;

import javax.swing.*;
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
public abstract class TreeNode extends PatchedDefaultMutableTreeNode implements Serializable {

    public String key;

    public boolean filter = false;

    public TreeNode(String key) {
        this.key = key;
    }

    public void updateNode() {
        recursiveOperation(TreeNode::setLabel);
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
        TreeNode node = getCloneNode();
        node.filter = filter;
        if (isContainer()) {
            for (Enumeration<?> e = children(); e.hasMoreElements(); ) {
                TreeNode currNode = (TreeNode) e.nextElement();
                node.add(currNode.clone());
            }
        }
        return node;
    }

    public abstract TreeNode getCloneNode();

    public abstract boolean isContainer();

    public void recursiveOperation(Consumer<TreeNode> consumer) {
        consumer.accept(this);
        for (Enumeration<?> e = children(); e.hasMoreElements(); ) {
            TreeNode child = (TreeNode) e.nextElement();
            child.recursiveOperation(consumer);
        }
    }

    public void expandByNode(Tree tree) {
        tree.expandPath(new TreePath(getPath()));
        TreeNode parent = getParent();
        if (parent != null) {
            parent.expandByNode(tree);
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

    public Icon icon() {
        return filter ? Icons.SELECT : displayIcon();
    }

    public abstract Icon displayIcon();

}
