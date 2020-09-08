package com.jsoneditor.buttons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.ui.AnActionButton;
import com.jsoneditor.TreeUtils;
import com.jsoneditor.moddle.Right;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/21 22:49
 */
public class Expand extends AnActionButton {

    private Right right;

    public Expand(Right right) {
        this.right = right;
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.Expandall);
        presentation.setText("expend");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        TreeUtils.expandTree(right.tree, new TreePath(right.getRoot()));
    }
}
