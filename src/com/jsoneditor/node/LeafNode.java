package com.jsoneditor.node;

import icons.Icons;

import javax.swing.*;

/**
 * @Description: 叶子节点
 * @Author: zhengt
 * @CreateDate: 2020/10/21 21:22
 */
public abstract class LeafNode extends TreeNode {

    protected LeafNode(String key) {
        super(key);
    }

    @Override
    public void setLabel() {
        TreeNode parent = getParent();
        if (parent instanceof ObjectNode) {
            setUserObject(key + " : " + valueString());
        } else if (parent instanceof ArrayNode) {
            setUserObject(parent.getIndex(this) + " : " + valueString());
        }
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public Icon displayIcon() {
        return Icons.AUTO;
    }

}
