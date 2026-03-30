package com.jsoneditor;

import com.jsoneditor.edits.TreeEdit;

import javax.swing.undo.UndoManager;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 22:58
 */
public class Undo {

    private static final UndoManager UNDO_MANAGER = new UndoManager();

    static {
        UNDO_MANAGER.setLimit(10);
    }

    public static void addAction(TreeEdit action) {
        UNDO_MANAGER.addEdit(action);
    }

    public static void undo() {
        try {
            UNDO_MANAGER.undo();
        } catch (Exception e) {
        }
    }

    public static void redo() {
        try {
            UNDO_MANAGER.redo();
        } catch (Exception ex) {
        }
    }

    public static void clear() {
        try {
            UNDO_MANAGER.discardAllEdits();
        } catch (Exception e) {
        }
    }
}
