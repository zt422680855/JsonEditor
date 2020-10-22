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

    private Project project;

    private ToolWindow toolWindow;

    private static Map<String, DefaultActionGroup[]> actionMap = new HashMap<>();

    public JsonEditorWindow(Project project, ToolWindow toolWindow) {
        setLayout(new GridBagLayout());

        this.project = project;
        ModdleContext.setProject(project);
        this.toolWindow = toolWindow;
        ModdleContext.setToolWindow(toolWindow);

        this.left = new Left(this);
        this.middle = new Middle(this);
        this.right = new Right(this);
        ModdleContext.addModdle(left, middle, right, this);
        ModdleContext.toRight();

        addTitleActions();
    }

    public void addTitleActions() {
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
        DefaultActionGroup otherAction = new DefaultActionGroup(new SwitchView(right, middle));
        DefaultActionGroup[] actions = new DefaultActionGroup[]{leftAction, rightAction, otherAction};
        actionMap.put(project.getName(), actions);
        ToolWindowEx ex = (ToolWindowEx) toolWindow;
        ex.setTitleActions(actions);
        setContext();
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

}
