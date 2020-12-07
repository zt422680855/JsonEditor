package com.jsoneditor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.jsoneditor.moddles.*;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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

        setResizeListener();
    }

    private void setResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int x = getWidth();
                int y = getHeight();
                int middleWidth = 30;
                int leftWidth = (int) Math.floor((x - middleWidth) * 0.6);
                int rightWidth = (int) Math.ceil((x - middleWidth) * 0.4);
                if (right.isShowing()) {
                    left.setSize(leftWidth, y);
                } else {
                    left.setSize(x, y);
                }
                left.setLocation(0, 0);
                middle.setSize(middleWidth, y);
                middle.setLocation(leftWidth, 0);
                right.setSize(rightWidth, y);
                right.setLocation(leftWidth + middleWidth, 0);
                JComponent c = left.getEditor().getContentComponent();
                if (c.hasFocus()) {
                    right.requestFocus();
                }
                c.requestFocus();
            }
        });
    }

}
