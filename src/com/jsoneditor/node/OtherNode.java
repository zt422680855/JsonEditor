package com.jsoneditor.node;

import com.jsoneditor.TreeNode;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/5 22:16
 */
public class OtherNode extends TreeNode {

    private Integer type = OTHER;

    public OtherNode() {

    }

    public OtherNode(String key, Object value) {
        super(key, value);
    }

}
