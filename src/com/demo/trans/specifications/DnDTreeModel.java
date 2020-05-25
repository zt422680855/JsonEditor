package com.demo.trans.specifications;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/25 22:49
 */
public class DnDTreeModel extends DefaultTreeModel {
    public DnDTreeModel(TreeNode root) {
        super(root);
    }

    public DnDTreeModel(TreeNode root, boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }

    /**
     * Removes the specified node. Note that the comparison is made absolutely,
     * ie. must be the exact same object not just equal.
     *
     * @param node
     */
    @Override
    public void removeNodeFromParent(MutableTreeNode node) {
        // get back the index of the node
        DnDNode parent = (DnDNode) node.getParent();
        int sourceIndex = 0;
        for (sourceIndex = 0; sourceIndex < parent.getChildCount() && parent
                .getChildAt(sourceIndex) != node; sourceIndex++) {
        }
        // time to perform the removal
        parent.remove(sourceIndex);
        // need a custom remove event because we manually removed
        // the correct node
        int[] childIndices = new int[1];
        childIndices[0] = sourceIndex;
        Object[] removedChildren = new Object[1];
        removedChildren[0] = node;
        this.nodesWereRemoved(parent, childIndices, removedChildren);
    }

}
