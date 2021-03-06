package com.jsoneditor.node;

import java.util.Enumeration;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:08
 */
public class ObjectNode extends TreeNode {

    public ObjectNode() {

    }

    public ObjectNode(String key, Object value) {
        super(key, value);
        updateNode();
    }

    @Override
    public void updateNode() {
        int childCount = getChildCount();
        TreeNode parent = getParent();
        if (parent == null || parent instanceof ObjectNode) {
            label = key + " : " + "{" + childCount + "}";
        } else if (parent instanceof ArrayNode) {
            label = parent.getIndex(this) + " : " + "{" + childCount + "}";
        }
        super.updateNode();
    }

    @Override
    public ObjectNode clone() {
        ObjectNode node = new ObjectNode(key, value);
        node.filter = this.filter;
        for (Enumeration<?> e = children(); e.hasMoreElements(); ) {
            TreeNode currNode = (TreeNode) e.nextElement();
            node.add(currNode.clone());
        }
        return node;
    }
}
