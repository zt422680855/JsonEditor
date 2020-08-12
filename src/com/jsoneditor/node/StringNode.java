package com.jsoneditor.node;

import com.jsoneditor.TreeNode;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:16
 */
public class StringNode extends TreeNode {

    public StringNode() {

    }

    public StringNode(String key, Object value) {
        super(key, value);
        updateNode();
    }

    @Override
    public void updateNode() {
        TreeNode parent = getParent();
        if (parent == null || parent instanceof ObjectNode) {
            label = key + " : " + (value != null ? value.toString() : "");
        } else if (parent instanceof ArrayNode) {
            label = parent.getIndex(this) + " : " + (value != null ? value.toString() : "");
        }
        super.updateNode();
    }

    @Override
    public StringNode clone() {
        return new StringNode(key, value);
    }
}
