package com.jsoneditor;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
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
 *
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
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(this, "", true);
        toolWindow.getContentManager().addContent(content);
        addActions(toolWindow);
    }

    private void addActions(@NotNull ToolWindow toolWindow) {
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
        ToolWindowEx ex = (ToolWindowEx) toolWindow;
        DefaultActionGroup otherAction = new DefaultActionGroup(new SwitchView(right, middle));
        ex.setTitleActions(leftAction, rightAction, otherAction);
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
