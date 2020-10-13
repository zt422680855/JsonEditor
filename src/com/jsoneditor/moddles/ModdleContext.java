package com.jsoneditor.moddles;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.jsoneditor.JsonEditorWindow;
import com.jsoneditor.TreeUtils;
import com.jsoneditor.node.TreeNode;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.List;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/28 21:42
 */
public class ModdleContext {

    private static Project project;

    private static ToolWindow toolWindow;

    private static JsonEditorWindow parent;

    private static Left left;

    private static Middle middle;

    private static Right right;

    public static void addModdle(JsonEditorModdle... moddles) {
        for (JsonEditorModdle moddle : moddles) {
            if (moddle instanceof JsonEditorWindow) {
                parent = (JsonEditorWindow) moddle;
            } else if (moddle instanceof Left) {
                left = (Left) moddle;
            } else if (moddle instanceof Middle) {
                middle = (Middle) moddle;
            } else if (moddle instanceof Right) {
                right = (Right) moddle;
            }
        }
        addListener();
    }

    public static void setProject(Project project) {
        ModdleContext.project = project;
    }

    public static Project getProject() {
        return project;
    }

    public static void setToolWindow(ToolWindow toolWindow) {
        ModdleContext.toolWindow = toolWindow;
    }

    public static ToolWindow getToolWindow() {
        return toolWindow;
    }

    public static JsonEditorWindow getParent() {
        return parent;
    }

    // left
    public static void setText(String text) {
        left.setText(text);
    }

    public static String getText() {
        return left.getText();
    }

    public static void resetScrollBarPosition() {
        left.resetScrollBarPosition();
    }

    public static void scrollToText(List<TreeNode> path) {
        left.scrollToText(path);
    }

    // middle
    public static void toRight() {
        middle.toRight();
    }

    public static void toLeft() {
        middle.toLeft();
    }

    public static void addListener() {
        middle.addListener();
    }

    // right
    public static TreeNode getRoot() {
        DefaultTreeModel model = (DefaultTreeModel) right.tree.getModel();
        return (TreeNode) model.getRoot();
    }

    public static void setRoot(TreeNode root) {
        DefaultTreeModel model = (DefaultTreeModel) right.tree.getModel();
        model.setRoot(root);
    }

    public static void expandTree() {
        TreeUtils.expandTree(right.tree, new TreePath(getRoot()));
    }

    public static void collapseTree() {
        TreeUtils.collapseTree(right.tree, new TreePath(getRoot()));
    }

    public static void expandNode(TreePath path) {
        right.tree.expandPath(path);
    }

    public static void updateTree() {
        right.tree.updateUI();
    }

}
