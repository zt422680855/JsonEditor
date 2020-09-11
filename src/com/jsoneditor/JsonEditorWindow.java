package com.jsoneditor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBPanel;
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

    public Left left;

    public Middle middle;

    public Right right;

    public Project project;

    public ToolWindow toolWindow;

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
    }

}
