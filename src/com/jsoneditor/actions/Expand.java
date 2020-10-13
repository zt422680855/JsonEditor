package com.jsoneditor.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.ui.AnActionButton;
import com.jsoneditor.TreeUtils;
import com.jsoneditor.moddles.ModdleContext;
import com.jsoneditor.moddles.Right;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/21 22:49
 */
public class Expand extends AnActionButton {

    public Expand() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.Expandall);
        presentation.setText("expend");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        ModdleContext.expandTree();
    }
}
