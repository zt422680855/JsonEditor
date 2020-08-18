package com.jsoneditor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
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

    public JsonEditor() {
        setLayout(new GridBagLayout());
        init();
    }

    private void init() {
        Left left = new Left(this);
        Middle middle = new Middle(this);
        Right right = new Right(this);
        middle.toRight(left, right);
        middle.toLeft(left, right);
        middle.syncToRight.doClick();
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        Content[] contents = contentManager.getContents();
        Content content = null;
        for (Content ct : contents) {
            if (project.getName().equals(ct.getDisplayName())) {
                content = ct;
            }
        }
        if (content == null) {
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            content = contentFactory.createContent(this, project.getName(), true);
            contentManager.addContent(content);
        } else {
            contentManager.setSelectedContent(content);
        }
        toolWindow.activate(null, true);
//        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
//        Content content = contentFactory.createContent(this, "JSON Editor", true);

//        toolWindow.getContentManager().addContent(content);
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
