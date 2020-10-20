package com.jsoneditor.node;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:16
 */
public class OtherNode extends TreeNode {

    public Object value;

    public OtherNode(String key, Object value) {
        super(key);
        this.value = value;
        updateNode();
    }

    @Override
    public void setLabel() {
        TreeNode parent = getParent();
        if (parent == null || parent instanceof ObjectNode) {
            label = key + " : " + (value != null ? value.toString() : null);
        } else if (parent instanceof ArrayNode) {
            label = parent.getIndex(this) + " : " + (value != null ? value.toString() : null);
        }
        setUserObject(this.label);
    }

    @Override
    public OtherNode clone() {
        OtherNode node = new OtherNode(key, value);
        node.filter = this.filter;
        return node;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "null";
    }
}
