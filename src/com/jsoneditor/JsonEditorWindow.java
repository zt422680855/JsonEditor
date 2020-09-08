package com.jsoneditor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.components.JBPanel;
import com.jsoneditor.buttons.*;
import com.jsoneditor.moddle.Left;
import com.jsoneditor.moddle.Middle;
import com.jsoneditor.moddle.Right;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

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

    private static Map<String, DefaultActionGroup[]> actionMap = new HashMap<>();

    public JsonEditorWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        setLayout(new GridBagLayout());

        this.left = new Left(this);
        this.middle = new Middle(this);
        this.right = new Right(this);
        middle.toRight(left, right);
        middle.toLeft(left, right);
        middle.syncToRight.doClick();
        addActions();
        setContext();
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
        actionMap.put(project.getName(), actions);
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

    // 本地测试用
    public static void main(String[] args) {
//        JsonEditor jsonEditor = new JsonEditor();
//        JFrame jFrame = new JFrame("JsonEditor");
//        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        jFrame.setSize(800, 500);
//        jFrame.setLocationRelativeTo(null);
//        jFrame.setVisible(true);
//        jFrame.add(jsonEditor);
    }

}
