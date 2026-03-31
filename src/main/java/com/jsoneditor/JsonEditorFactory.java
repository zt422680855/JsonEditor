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
        String title1 = "tab 1", title2 = "tab 2", title3 = "tab 3", title4 = "tab 4", title5 = "tab 5";
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content tab1 = contentFactory.createContent(getDisplayPanel(project, toolWindow, title1), title1, false);
        Content tab2 = contentFactory.createContent(getDisplayPanel(project, toolWindow, title2), title2, false);
        Content tab3 = contentFactory.createContent(getDisplayPanel(project, toolWindow, title3), title3, false);
        Content tab4 = contentFactory.createContent(getDisplayPanel(project, toolWindow, title4), title4, false);
        Content tab5 = contentFactory.createContent(getDisplayPanel(project, toolWindow, title5), title5, false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(tab1);
        contentManager.addContent(tab2);
        contentManager.addContent(tab3);
        contentManager.addContent(tab4);
        contentManager.addContent(tab5);
    }

    private SimpleToolWindowPanel getDisplayPanel(@NotNull Project project, @NotNull ToolWindow toolWindow, String title) {
        SimpleToolWindowPanel content = new SimpleToolWindowPanel(true);
        content.setLayout(new BorderLayout());
        JsonEditorWindow jsonEditor = new JsonEditorWindow(project, toolWindow, title);
        content.add(jsonEditor, BorderLayout.CENTER);
        setToolBar(content, jsonEditor);
        return content;
    }

    private void setToolBar(SimpleToolWindowPanel content, JsonEditorWindow jsonEditor) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new Format(jsonEditor));
        group.add(new Compress(jsonEditor));
        group.add(new Reset(jsonEditor));
        group.addSeparator();
        group.add(new Expand(jsonEditor));
        group.add(new Close(jsonEditor));
        group.add(new Back(jsonEditor));
        group.add(new Forward(jsonEditor));
        group.add(new SwitchView(jsonEditor));
        group.addSeparator();
        ActionToolbar toolBar = ActionManager.getInstance().createActionToolbar("jsonEditorToolbar", group, true);
        toolBar.setTargetComponent(jsonEditor);
        content.setToolbar(JBUI.Panels.simplePanel(toolBar.getComponent()));
    }
}
