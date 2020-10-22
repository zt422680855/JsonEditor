package com.jsoneditor;

import com.intellij.openapi.project.Project;
import com.jsoneditor.edits.TreeEdit;

import javax.swing.undo.UndoManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 22:58
 */
public class Undo {

    private static Map<Project, UndoManager> undoManagerMap = new ConcurrentHashMap<>();

    public static void addAction(Project project, TreeEdit action) {
        UndoManager undoManager = undoManagerMap.get(project);
        if (undoManager == null) {
            undoManager = new UndoManager();
            undoManager.setLimit(10);
            undoManagerMap.put(project, undoManager);
        }
        undoManager.addEdit(action);
    }

    public static void undo(Project project) {
        try {
            UndoManager undoManager = undoManagerMap.get(project);
            if (undoManager != null && undoManager.canUndo()) {
                undoManager.undo();
            }
        } catch (Exception ex) {
        }
    }

    public static void redo(Project project) {
        try {
            UndoManager undoManager = undoManagerMap.get(project);
            if (undoManager != null && undoManager.canRedo()) {
                undoManager.redo();
            }
        } catch (Exception ex) {
        }
    }

    public static void clear(Project project) {
        UndoManager undoManager = undoManagerMap.get(project);
        if (undoManager != null) {
            undoManager.discardAllEdits();
        }
    }
}
