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
    public String valueString() {
        return value != null ? value.toString() : "null";
    }
}
