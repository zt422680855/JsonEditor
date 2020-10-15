package com.jsoneditor.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.ui.AnActionButton;
import com.jsoneditor.Undo;
import org.jetbrains.annotations.NotNull;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/21 22:10
 */
public class Forward extends AnActionButton {

    public Forward() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.Redo);
        presentation.setText("forward");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        Undo.redo();
    }
}
