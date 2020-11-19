package com.jsoneditor.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AnActionButton;
import com.jsoneditor.moddles.Left;
import com.jsoneditor.moddles.Middle;
import com.jsoneditor.moddles.ModdleContext;
import com.jsoneditor.moddles.Right;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/24 22:26
 */
public class SwitchView extends AnActionButton {

    private volatile boolean isShow = true;

    public SwitchView() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(Icons.SHOW);
        presentation.setText("view");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Middle middle = ModdleContext.getMiddle(project);
        Left left = ModdleContext.getLeft(project);
        Right right = ModdleContext.getRight(project);
        right.setVisible(!right.isShowing());
        middle.setVisible(!middle.isShowing());

        if (isShow) {
            isShow = false;
            left.setSize(left.parent.getWidth(), left.parent.getHeight());
        } else {
            isShow = true;
            left.setSize(left.parent.getWidth() - middle.getWidth() - right.getWidth(), left.parent.getHeight());
        }
    }

}
