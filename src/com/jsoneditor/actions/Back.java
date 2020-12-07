package com.jsoneditor.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.jsoneditor.Undo;
import org.jetbrains.annotations.NotNull;

/**
 * @Description: 后退
 * @Author: zhengt
 * @CreateDate: 2020/8/21 22:07
 */
public class Back extends AnAction {

    public Back() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.Undo);
        presentation.setText("back");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Undo.undo(project);
    }
}
