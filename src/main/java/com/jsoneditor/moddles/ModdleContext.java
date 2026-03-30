package com.jsoneditor.moddles;

import com.intellij.json.psi.JsonElement;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.jsoneditor.JsonEditorWindow;
import com.jsoneditor.TreeUtils;
import com.jsoneditor.node.TreeNode;

import javax.swing.tree.TreePath;
import java.util.List;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/28 21:42
 */
public class ModdleContext {

    private static final ModdleContext INSTANCE = new ModdleContext();
    ;

    private Project project;

    private JsonEditorWindow parent;

    private Left left;

    private Middle middle;

    private Right right;

    private String selectTab;

    public static ModdleContext getInstance() {
        return INSTANCE;
    }

    public static void addModdles(Project project, JsonEditorModdle... moddles) {
        ModdleContext ctx = getInstance();
        ctx.project = project;
        for (JsonEditorModdle moddle : moddles) {
            if (moddle instanceof JsonEditorWindow) {
                ctx.parent = (JsonEditorWindow) moddle;
            } else if (moddle instanceof Left) {
                ctx.left = (Left) moddle;
            } else if (moddle instanceof Middle) {
                ctx.middle = (Middle) moddle;
            } else if (moddle instanceof Right) {
                ctx.right = (Right) moddle;
            }
        }
    }

    public static JsonEditorWindow getParent() {
        return getInstance().parent;
    }

    /* left */
    public static Left getLeft() {
        return getInstance().left;
    }

    public static EditorEx getEditor() {
        return getLeft().getEditor();
    }

    public static void setText(String text) {
        getLeft().textPanel.setText(text);
    }

    public static String getText() {
        return getLeft().textPanel.getText();
    }

    public static void resetScrollBarPosition() {
        getLeft().textPanel.resetScrollBarPosition();
    }

    public static void formatCode() {
        getLeft().textPanel.format();
    }

    public static void scrollToText(List<TreeNode> path) {
        getLeft().textPanel.scrollToText(path);
    }

    /* middle */
    public static Middle getMiddle() {
        return getInstance().middle;
    }

    public static void toRight() {
        getMiddle().toRight();
    }

    public static void toLeft() {
        getMiddle().toLeft();
    }

    public static void addListener() {
        getMiddle().addListener();
    }

    /* right */
    public static Right getRight() {
        return getInstance().right;
    }

    public static TreeNode getRoot() {
        return getRight().getRoot();
    }

    public static void setRoot(TreeNode root) {
        getRight().setRoot(root);
    }

    public static void expandTree() {
        TreeUtils.expandTree(getRight().tree, new TreePath(getRoot()));
    }

    public static void collapseTree() {
        TreeUtils.collapseTree(getRight().tree, new TreePath(getRoot()));
    }

    public static void expandNode(TreePath path) {
        getRight().tree.expandPath(path);
    }

    public static void updateTree() {
        getRight().tree.updateUI();
    }

    public static void scrollToTreeNode(List<JsonElement> elements) {
        getRight().scrollToTreeNode(elements);
    }

}
