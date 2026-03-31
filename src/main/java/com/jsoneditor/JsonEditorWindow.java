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

    private String title;

    private final ToolWindow toolWindow;

    public JsonEditorWindow(Project project, ToolWindow toolWindow, String title) {
        super(project);
        this.title = title;
        setLayout(null);

        this.project = project;
        this.toolWindow = toolWindow;

        super.ctx = new ModdleContext();
        this.ctx.initModdles(project, this);
        this.ctx.addListener();
        this.ctx.toRight();

        setResizeListener();
    }

    public String getTitle() {
        return title;
    }

    private void setResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Left left = ctx.getLeft();
                Middle middle = ctx.getMiddle();
                Right right = ctx.getRight();
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
