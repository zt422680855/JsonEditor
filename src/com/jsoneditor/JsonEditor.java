package com.jsoneditor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.jsoneditor.layout.Left;
import com.jsoneditor.layout.Middle;
import com.jsoneditor.layout.Right;
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

    private GridBagLayout layout = new GridBagLayout();

    private Left left;

    private Middle middle;

    private Right right;

    public JsonEditor() {
        setLayout(layout);
        init();
    }

    private void init() {
        this.left = new Left(this);
        this.middle = new Middle(this);
        this.right = new Right(this);
        left.reset(() -> {
            middle.syncToRight.doClick();
            Undo.clear();
        });
        middle.toRight(left, right);
        middle.toLeft(left, right);
        middle.syncToRight.doClick();
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(this, "", true);
        content.setCloseable(true);
        toolWindow.getContentManager().addContent(content);
    }

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
