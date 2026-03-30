package com.jsoneditor.edits;

import com.intellij.ui.treeStructure.Tree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;

/**
 * @Description: 操作树的每个动作，包括新增节点、替换节点、删除节点
 * @Author: zhengt
 * @CreateDate: 2020/8/11 22:10
 */
public abstract class TreeEdit extends AbstractUndoableEdit {

    protected Tree tree;

    protected DefaultTreeModel treeModel;

    public TreeEdit(Tree tree) {
        this.tree = tree;
        this.treeModel = (DefaultTreeModel) tree.getModel();
    }

    public abstract void doAction();

    @Override
    public void redo() throws CannotRedoException {
        doAction();
        super.redo();
    }
}
