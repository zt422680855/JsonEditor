package com.jsoneditor.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.jsoneditor.Undo;
import org.jetbrains.annotations.NotNull;

/**
 * @Description: 前进
 * @Author: zhengt
 * @CreateDate: 2020/8/21 22:10
 */
public class Forward extends AnAction {

    public Forward() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.Redo);
        presentation.setText("forward");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Undo.redo(project);
    }
}
