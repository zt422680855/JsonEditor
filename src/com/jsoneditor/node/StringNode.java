package com.jsoneditor.node;

import com.jsoneditor.TreeNode;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:16
 */
public class StringNode extends TreeNode {

    private Integer type = STRING;

    public StringNode() {

    }

    public StringNode(String key, Object value) {
        super(key, value);
    }

}
