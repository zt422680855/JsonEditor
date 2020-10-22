package com.jsoneditor.node;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:16
 */
public class OtherNode extends LeafNode {

    public Object value;

    public OtherNode(String key, Object value) {
        super(key);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public OtherNode clone() {
        OtherNode node = new OtherNode(key, value);
        node.filter = this.filter;
        return node;
    }

}
