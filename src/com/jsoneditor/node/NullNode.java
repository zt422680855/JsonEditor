package com.jsoneditor.node;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/10/20 22:31
 */
public final class NullNode extends OtherNode {

    public NullNode(String key) {
        super(key, null);
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String valueString() {
        return "null";
    }

    @Override
    public NullNode clone() {
        NullNode node = new NullNode(key);
        node.filter = this.filter;
        return node;
    }
}
