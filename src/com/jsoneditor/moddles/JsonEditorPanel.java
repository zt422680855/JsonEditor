package com.jsoneditor.moddles;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.AnActionButton;
import com.jsoneditor.actions.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/13 12:19
 */
public class JsonEditorPanel extends JSplitPane {

    private Left left;

    private RightPanel rightPanel;

    private Project project;

    private ToolWindow toolWindow;

    private static Map<String, DefaultActionGroup[]> actionMap = new HashMap<>();

    public JsonEditorPanel(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        this.left = new Left(project);
        this.rightPanel = new RightPanel();
        setLeftComponent(this.left);
        setRightComponent(this.rightPanel);
        rightPanel.middle.addListener(left, rightPanel.right);
        toRight();
        addTitleActions();
        setContext();
        setDividerLocation(0.4);
        setOneTouchExpandable(true);
        setContinuousLayout(true);
        setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    }

    private void addTitleActions() {
        Format format = new Format(left);
        Compress compress = new Compress(left);
        Reset reset = new Reset(left);
        DefaultActionGroup leftAction = new DefaultActionGroup(format, compress, reset);
        leftAction.addSeparator();
        Expand expand = new Expand(rightPanel.right);
        Close close = new Close(rightPanel.right);
        Back back = new Back();
        Forward forward = new Forward();
        DefaultActionGroup rightAction = new DefaultActionGroup(expand, close, back, forward);
        rightAction.addSeparator();
        DefaultActionGroup otherAction = new DefaultActionGroup(new SwitchView(rightPanel.right, rightPanel.middle));
        DefaultActionGroup[] actions = new DefaultActionGroup[]{leftAction, rightAction, otherAction};
        actionMap.put(project.getName(), actions);
    }

    public void setContext() {
        DefaultActionGroup[] groups = actionMap.get(project.getName());
        ToolWindowEx ex = (ToolWindowEx) toolWindow;
        ex.setTitleActions(groups);
        for (DefaultActionGroup g : groups) {
            AnAction[] children = g.getChildren(null);
            for (AnAction child : children) {
                if (child instanceof AnActionButton) {
                    ((AnActionButton) child).setContextComponent(this);
                }
            }
        }
    }

    public void setText(String text) {
        left.setText(text);
    }

    public void toRight() {
        rightPanel.middle.toRight();
    }

    public void toLeft() {
        rightPanel.middle.toLeft();
    }

}
