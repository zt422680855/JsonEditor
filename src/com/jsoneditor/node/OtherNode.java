package com.jsoneditor.node;

import com.jsoneditor.TreeNode;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:16
 */
public class OtherNode extends TreeNode {

    public OtherNode() {

    }

    public OtherNode(String key, Object value) {
        super(key, value);
        updateNode();
    }

    @Override
    public void updateNode() {
        TreeNode parent = getParent();
        if (parent == null || parent instanceof ObjectNode) {
            label = key + " : " + value;
        } else if (parent instanceof ArrayNode) {
            label = parent.getIndex(this) + " : " + (value != null ? value.toString() : "");
        }
        super.updateNode();
    }

    @Override
    public OtherNode clone() {
        return new OtherNode(key, value);
    }
}
