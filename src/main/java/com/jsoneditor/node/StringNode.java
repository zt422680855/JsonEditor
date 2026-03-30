package com.jsoneditor.node;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:16
 */
public final class StringNode extends LeafNode {

    public String value;

    public StringNode(String key, String value) {
        super(key);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public StringNode getCloneNode() {
        return new StringNode(key, value);
    }

}
