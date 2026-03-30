package com.jsoneditor.edits;

import com.intellij.ui.treeStructure.Tree;

import javax.swing.tree.TreePath;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * @Description: 树节点拖拽
 * @Author: zhengt
 * @CreateDate: 2020/8/12 22:37
 */
public class DragEdit extends TreeEdit {

    private AddEdit addEdit;

    private DeleteEdit deleteEdit;

    public DragEdit(Tree tree, AddEdit addEdit, DeleteEdit deleteEdit) {
        super(tree);
        this.addEdit = addEdit;
        this.deleteEdit = deleteEdit;
    }

    @Override
    public void doAction() {
        addEdit.doAction();
        tree.setSelectionPath(new TreePath(addEdit.target.getPath()));
        deleteEdit.doAction();
    }

    @Override
    public void undo() throws CannotUndoException {
        addEdit.undo();
        deleteEdit.undo();
        super.undo();
    }

    @Override
    public void redo() throws CannotRedoException {
        addEdit.redo();
        deleteEdit.redo();
    }

    @Override
    public boolean canUndo() {
        return addEdit.canUndo() && deleteEdit.canUndo();
    }

    @Override
    public boolean canRedo() {
        return addEdit.canRedo() && deleteEdit.canRedo();
    }
}
