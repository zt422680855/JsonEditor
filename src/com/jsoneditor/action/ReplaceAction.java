package com.jsoneditor.action;

import com.intellij.ui.treeStructure.Tree;
import com.jsoneditor.TreeNode;

import javax.swing.tree.TreePath;
import javax.swing.undo.CannotUndoException;

/**
 * @Description:
 * 1、新增子节点时，如果父节点不是对象或数组类型，会发生替换节点操作
 * 2、编辑节点时会发生替换操作
 * @Author: zhengt
 * @CreateDate: 2020/8/12 21:23
 */
public class ReplaceAction extends TreeAction {

    // 待添加的节点
    private TreeNode target;

    // 被替换的节点
    private TreeNode source;

    // 是否保留子节点
    private boolean keepChildren;

    public ReplaceAction(Tree tree, TreeNode target, TreeNode source, boolean keepChildren) {
        super(tree);
        this.target = target;
        this.source = source;
        this.keepChildren = keepChildren;
    }

    @Override
    public void doAction() {
        if (keepChildren) {
            target.attachChildrenFromAnotherNode(source);
        }
        TreeNode parent = source.getParent();
        treeModel.insertNodeInto(target, parent, parent.getIndex(source));
        treeModel.removeNodeFromParent(source);
        target.updateNode();
        tree.setSelectionPath(new TreePath(target.getPath()));
        tree.expandPath(new TreePath(target.getPath()));
    }

    @Override
    public void undo() throws CannotUndoException {
        TreeNode parent = target.getParent();
        if (keepChildren) {
            source.attachChildrenFromAnotherNode(target);
        }
        treeModel.insertNodeInto(source, parent, parent.getIndex(target));
        treeModel.removeNodeFromParent(target);
        parent.updateNode();
    }
}
