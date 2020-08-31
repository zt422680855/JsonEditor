package com.jsoneditor.buttons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.jsoneditor.moddle.Middle;
import com.jsoneditor.moddle.Right;
import org.jetbrains.annotations.NotNull;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/24 22:26
 */
public class SwitchView extends AnAction {

    private Right right;

    private Middle middle;

    public SwitchView(Right right, Middle middle) {
        this.right = right;
        this.middle = middle;
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.Diff);
        presentation.setText("view");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        right.setVisible(!right.isShowing());
        middle.setVisible(!middle.isShowing());
    }

}
