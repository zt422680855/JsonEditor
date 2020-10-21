package com.jsoneditor.node;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:16
 */
public class StringNode extends TreeNode {

    public String value;

    public StringNode(String key, String value) {
        super(key);
        this.value = value;
//        updateNode();
    }

    @Override
    public void setLabel() {
        TreeNode parent = getParent();
        if (parent instanceof ObjectNode) {
            label = key + " : " + value;
        } else if (parent instanceof ArrayNode) {
            label = parent.getIndex(this) + " : " + value;
        }
        setUserObject(this.label);
    }

    @Override
    public StringNode clone() {
        StringNode node = new StringNode(key, value);
        node.filter = filter;
        return node;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
