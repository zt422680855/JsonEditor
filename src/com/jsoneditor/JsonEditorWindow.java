package com.jsoneditor;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.components.JBPanel;
import com.jsoneditor.actions.*;
import com.jsoneditor.moddles.Left;
import com.jsoneditor.moddles.Middle;
import com.jsoneditor.moddles.Right;

import java.awt.*;

/**
 * @Description: 开发时iml文件module节点的type要等于PLUGIN_MODULE。
 * @Author: zhengtao
 * @CreateDate: 2020/5/7 22:44
 */
public class JsonEditorWindow extends JBPanel {

    private Left left;

    private Middle middle;

    private Right right;

    private Project project;

    private ToolWindow toolWindow;

    public JsonEditorWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        setLayout(new GridBagLayout());

        this.left = new Left(this, project);
        this.middle = new Middle(this);
        this.right = new Right(this);
        middle.toRight(left, right);
        middle.toLeft(left, right);
        middle.syncToRight.doClick();
        addActions();
    }

    private void addActions() {
        Format format = new Format(left);
        Compress compress = new Compress(left);
        Reset reset = new Reset(left);
        DefaultActionGroup leftAction = new DefaultActionGroup(format, compress, reset);
        leftAction.addSeparator();
        Expand expand = new Expand(right);
        Close close = new Close(right);
        Back back = new Back();
        Forward forward = new Forward();
        DefaultActionGroup rightAction = new DefaultActionGroup(expand, close, back, forward);
        rightAction.addSeparator();
        DefaultActionGroup otherAction = new DefaultActionGroup(new SwitchView(right, middle));
        DefaultActionGroup[] actions = new DefaultActionGroup[]{leftAction, rightAction, otherAction};
        ToolWindowEx ex = (ToolWindowEx) toolWindow;
        ex.setTitleActions(actions);
    }

}
