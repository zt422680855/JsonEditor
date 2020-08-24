package com.jsoneditor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.jsoneditor.buttons.*;
import com.jsoneditor.moddle.Left;
import com.jsoneditor.moddle.Middle;
import com.jsoneditor.moddle.Right;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @Description: 开发时iml文件module节点的type要等于PLUGIN_MODULE。
 * TODO
 * 1. 按钮大小变化
 * 2. 代码整理、重构
 * @Author: zhengtao
 * @CreateDate: 2020/5/7 22:44
 */
public class JsonEditor extends JBPanel implements ToolWindowFactory {

    private Left left;

    private Middle middle;

    private Right right;

    public JsonEditor() {
        setLayout(new GridBagLayout());
        init();
    }

    private void init() {
        this.left = new Left(this);
        this.middle = new Middle(this);
        this.right = new Right(this);
        middle.toRight(left, right);
        middle.toLeft(left, right);
        middle.syncToRight.doClick();
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        right.setVisible(false);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(this, "", true);
        toolWindow.getContentManager().addContent(content);
        addActions(toolWindow);
    }

    private void addActions(@NotNull ToolWindow toolWindow) {
        Format format = new Format(left);
        Compress compress = new Compress(left);
        Reset reset = new Reset(left);
        Expand expand = new Expand(right);
        Close close = new Close(right);
        Back back = new Back();
        Forward forward = new Forward();
        ToolWindowEx ex = (ToolWindowEx) toolWindow;
        ex.setTitleActions(format, compress, reset, expand, close, back, forward);
    }

    // 本地测试用
    public static void main(String[] args) {
        JsonEditor jsonEditor = new JsonEditor();
        JFrame jFrame = new JFrame("JsonEditor");
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(800, 500);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.add(jsonEditor);
    }

}
