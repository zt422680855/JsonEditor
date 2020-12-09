package com.jsoneditor;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.ui.JBUI;
import com.jsoneditor.actions.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/8 21:38
 */
public class JsonEditorFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        SimpleToolWindowPanel panel = getDisplayPanel(project, toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, project.getName(), false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(content);
    }

    private SimpleToolWindowPanel getDisplayPanel(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        SimpleToolWindowPanel content = new SimpleToolWindowPanel(true);
        content.setLayout(new BorderLayout());
        JsonEditorWindow jsonEditor = new JsonEditorWindow(project, toolWindow);
        content.add(jsonEditor, BorderLayout.CENTER);
        setToolBar(content, jsonEditor);
        return content;
    }

    private void setToolBar(SimpleToolWindowPanel content, JsonEditorWindow jsonEditor) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new Format());
        group.add(new Compress());
        group.add(new Reset());
        group.addSeparator();
        group.add(new Expand());
        group.add(new Close());
        group.add(new Back());
        group.add(new Forward());
        group.addSeparator();
        group.add(new SwitchView());
        ActionToolbar toolBar = ActionManager.getInstance().createActionToolbar("jsonEditorToolbar", group, true);
        toolBar.setTargetComponent(jsonEditor);
        content.setToolbar(JBUI.Panels.simplePanel(toolBar.getComponent()));
    }
}
