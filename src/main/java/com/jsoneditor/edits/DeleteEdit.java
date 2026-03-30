package com.jsoneditor.edits;

import com.intellij.ui.treeStructure.Tree;
import com.jsoneditor.node.TreeNode;

import javax.swing.undo.CannotUndoException;
import java.util.LinkedList;
import java.util.List;

/**
 * @Description: 删除节点
 * @Author: zhengt
 * @CreateDate: 2020/8/12 23:46
 */
public class DeleteEdit extends TreeEdit {

    // 待删除的节点
    private List<NodeInfo> nodes;

    public DeleteEdit(Tree tree, TreeNode[] targetList) {
        super(tree);
        nodes = new LinkedList<>();
        for (TreeNode node : targetList) {
            nodes.add(new NodeInfo(node));
        }
    }

    @Override
    public void doAction() {
        nodes.forEach(nodeInfo -> {
            treeModel.removeNodeFromParent(nodeInfo.target);
            nodeInfo.parent.updateNode();
        });
    }

    @Override
    public void undo() throws CannotUndoException {
        nodes.forEach(nodeInfo -> {
            TreeNode parent = nodeInfo.parent;
            treeModel.insertNodeInto(nodeInfo.target, parent, nodeInfo.index);
            parent.updateNode();
        });
        super.undo();
    }

    class NodeInfo {

        private TreeNode target;

        private TreeNode parent;

        private int index;

        NodeInfo(TreeNode target) {
            this.target = target;
            this.parent = target.getParent();
            this.index = parent.getIndex(target);
        }
    }

}
