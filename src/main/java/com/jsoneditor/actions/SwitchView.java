package com.jsoneditor.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.jsoneditor.moddles.Left;
import com.jsoneditor.moddles.Middle;
import com.jsoneditor.moddles.ModdleContext;
import com.jsoneditor.moddles.Right;
import org.jetbrains.annotations.NotNull;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/24 22:26
 */
public class SwitchView extends AnAction {

    private volatile boolean isShow = true;

    public SwitchView() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.GroupBy);
        presentation.setText("view");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Middle middle = ModdleContext.getMiddle();
        Left left = ModdleContext.getLeft();
        Right right = ModdleContext.getRight();
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
