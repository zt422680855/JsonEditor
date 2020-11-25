package com.jsoneditor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.AnActionButton;
import com.jsoneditor.actions.*;
import com.jsoneditor.moddles.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 开发时iml文件module节点的type要等于PLUGIN_MODULE。
 * @Author: zhengtao
 * @CreateDate: 2020/5/7 22:44
 */
public class JsonEditorWindow extends JsonEditorModdle {

    private Left left;

    private Middle middle;

    private Right right;

    private ToolWindow toolWindow;

    private static Map<Project, DefaultActionGroup[]> actionMap = new HashMap<>();

    public JsonEditorWindow(Project project, ToolWindow toolWindow) {
        super(project);
        setLayout(null);

        this.project = project;
        this.toolWindow = toolWindow;

        this.left = new Left(project, this);
        this.middle = new Middle(project, this);
        this.right = new Right(project, this);

        add(left);
        add(middle);
        add(right);

        ModdleContext.addModdle(project, left, middle, right, this);
        ModdleContext.addListener(project);
        ModdleContext.toRight(project);

        addTitleActions();

        setResizeListener();
    }

    private void addTitleActions() {
        Format format = new Format();
        Compress compress = new Compress();
        Reset reset = new Reset();
        DefaultActionGroup leftAction = new DefaultActionGroup(format, compress, reset);
        leftAction.addSeparator();
        Expand expand = new Expand();
        Close close = new Close();
        Back back = new Back();
        Forward forward = new Forward();
        DefaultActionGroup rightAction = new DefaultActionGroup(expand, close, back, forward);
        rightAction.addSeparator();
        DefaultActionGroup otherAction = new DefaultActionGroup(new SwitchView());
        DefaultActionGroup[] actions = new DefaultActionGroup[]{leftAction, rightAction, otherAction};
        actionMap.put(project, actions);
        setContext();
    }

    public void setContext() {
        DefaultActionGroup[] groups = actionMap.get(project);
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

    private void setResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setComponent();
            }
        });
    }

    private void setComponent() {
        int x = getWidth();
        int y = getHeight();
        int middleWidth = 30;
        int leftWidth = (int) Math.floor((x - middleWidth) * 0.6);
        int rightWidth = (int) Math.ceil((x - middleWidth) * 0.4);
        left.setSize(leftWidth, y);
        middle.setSize(middleWidth, y);
        right.setSize(rightWidth, y);
        left.setLocation(0, 0);
        middle.setLocation(leftWidth, 0);
        right.setLocation(leftWidth + middleWidth, 0);
        left.getEditor().getContentComponent().requestFocus();
    }

}
