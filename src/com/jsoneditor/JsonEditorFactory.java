package com.jsoneditor;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.jsoneditor.actions.*;
import org.jetbrains.annotations.NotNull;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/8 21:38
 */
public class JsonEditorFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JsonEditorWindow jsonEditor = new JsonEditorWindow(project, toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(jsonEditor, "", true);
        toolWindow.getContentManager().addContent(content);
        addActions(toolWindow, jsonEditor);
    }

    private void addActions(@NotNull ToolWindow toolWindow, JsonEditorWindow jsonEditor) {
        Format format = new Format(jsonEditor.left);
        Compress compress = new Compress(jsonEditor.left);
        Reset reset = new Reset(jsonEditor.left);
        DefaultActionGroup leftAction = new DefaultActionGroup(format, compress, reset);
        leftAction.addSeparator();
        Expand expand = new Expand(jsonEditor.right);
        Close close = new Close(jsonEditor.right);
        Back back = new Back();
        Forward forward = new Forward();
        DefaultActionGroup rightAction = new DefaultActionGroup(expand, close, back, forward);
        rightAction.addSeparator();
        DefaultActionGroup otherAction = new DefaultActionGroup(new SwitchView(jsonEditor.right, jsonEditor.middle));
        DefaultActionGroup[] actions = new DefaultActionGroup[]{leftAction, rightAction, otherAction};
        ToolWindowEx ex = (ToolWindowEx) toolWindow;
        ex.setTitleActions(actions);
    }
}
