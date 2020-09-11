package com.jsoneditor.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.ui.AnActionButton;
import com.jsoneditor.Undo;
import org.jetbrains.annotations.NotNull;

import javax.swing.undo.CannotUndoException;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/21 22:07
 */
public class Back extends AnActionButton {

    public Back() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.Undo);
        presentation.setText("back");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        try {
            Undo.undo();
        } catch (CannotUndoException ex) {
        }
    }
}
