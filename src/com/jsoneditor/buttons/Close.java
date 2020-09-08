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
 * @CreateDate: 2020/8/21 22:53
 */
public class Close extends AnActionButton {

    private Right right;

    public Close(Right right) {
        this.right = right;
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.Collapseall);
        presentation.setText("close");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        TreeUtils.collapseTree(right.tree, new TreePath(right.getRoot()));
    }
}
