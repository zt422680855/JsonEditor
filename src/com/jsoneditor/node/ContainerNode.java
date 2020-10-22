package com.jsoneditor.node;

/**
 * @Description: 容器节点，对象或数组
 * @Author: zhengt
 * @CreateDate: 2020/10/21 21:05
 */
public abstract class ContainerNode extends TreeNode {

    protected ContainerNode(String key) {
        super(key);
    }

    @Override
    public void setLabel() {
        int childCount = getChildCount();
        TreeNode parent = getParent();
        if (isRoot() || parent instanceof ObjectNode) {
            setUserObject(String.format(getFormatter(), key, childCount));
        } else if (parent instanceof ArrayNode) {
            setUserObject(String.format(getFormatter(), String.valueOf(parent.getIndex(this)), childCount));
        }
    }

    public abstract String getFormatter();

}
