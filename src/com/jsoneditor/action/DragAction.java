package com.jsoneditor.action;

import com.intellij.ui.treeStructure.Tree;

import javax.swing.tree.TreePath;
import javax.swing.undo.CannotUndoException;

/**
 * @Description: 树节点拖拽
 * @Author: zhengt
 * @CreateDate: 2020/8/12 22:37
 */
public class DragAction extends TreeAction {

    private AddAction addAction;

    private DeleteAction delAction;

    public DragAction(Tree tree, AddAction addAction, DeleteAction delAction) {
        super(tree);
        this.addAction = addAction;
        this.delAction = delAction;
    }

    @Override
    public void doAction() {
        addAction.doAction();
        tree.setSelectionPath(new TreePath(addAction.target.getPath()));
        delAction.doAction();
    }

    @Override
    public void undo() throws CannotUndoException {
        addAction.undo();
        delAction.undo();
    }
}
