package com.jsoneditor.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.jsoneditor.JsonEditorWindow;
import org.jetbrains.annotations.NotNull;

/**
 * @Description: 关闭右边的JTree
 * @Author: zhengt
 * @CreateDate: 2020/8/21 22:53
 */
public class Close extends BaseAction {

    public Close(JsonEditorWindow jsonEditor) {
        super(jsonEditor);
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.Collapseall);
        presentation.setText("close");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        getCtx().collapseTree();
    }
}
