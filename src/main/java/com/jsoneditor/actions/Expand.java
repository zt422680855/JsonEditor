package com.jsoneditor.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.jsoneditor.JsonEditorWindow;
import org.jetbrains.annotations.NotNull;

/**
 * @Description: 展开右边的JTree
 * @Author: zhengt
 * @CreateDate: 2020/8/21 22:49
 */
public class Expand extends BaseAction {

    public Expand(JsonEditorWindow jsonEditor) {
        super(jsonEditor);
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.Expandall);
        presentation.setText("expend");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        getCtx().expandTree();
    }
}
