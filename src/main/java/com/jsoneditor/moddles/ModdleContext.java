package com.jsoneditor.moddles;

import com.intellij.json.psi.JsonElement;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.jsoneditor.JsonEditorWindow;
import com.jsoneditor.TreeUtils;
import com.jsoneditor.node.TreeNode;
import com.jsoneditor.persist.JsonEditorPersistentState;

import javax.swing.tree.TreePath;
import java.util.List;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/28 21:42
 */
public class ModdleContext {

    private Project project;

    private JsonEditorPersistentState state;

    private JsonEditorWindow window;

    private Left left;

    private Middle middle;

    private Right right;

    private String storeKey;

    public void initModdles(Project project, JsonEditorWindow window) {
        this.project = project;
        this.window = window;
        this.state = project.getService(JsonEditorPersistentState.class);
        this.left = new Left(project, window);
        this.middle = new Middle(project, window);
        this.right = new Right(project, window);
        window.add(left);
        window.add(middle);
        window.add(right);
        this.storeKey = project.getName() + "_" + this.window.getTitle();
        setText(loadText());
    }

    public JsonEditorWindow getWindow() {
        return this.window;
    }

    public JsonEditorPersistentState getState() {
        return this.state;
    }

    public Left getLeft() {
        return this.left;
    }

    public EditorEx getEditor() {
        return getLeft().getEditor();
    }

    public void setText(String text) {
        getLeft().textPanel.setText(text);
    }

    public String getText() {
        return getLeft().textPanel.getText();
    }

    public void storeText(String text) {
        JsonEditorWindow p = getWindow();
        JsonEditorPersistentState s = getState();
        if (p != null && s != null) {
            s.setText(this.storeKey, text);
        }
    }

    public String loadText() {
        JsonEditorWindow p = getWindow();
        JsonEditorPersistentState s = getState();
        if (p != null && s != null) {
            return s.getText(this.storeKey);
        }
        return "";
    }

    public void resetScrollBarPosition() {
        getLeft().textPanel.resetScrollBarPosition();
    }

    public void formatCode() {
        getLeft().textPanel.format();
    }

    public void scrollToText(List<TreeNode> path) {
        getLeft().textPanel.scrollToText(path);
    }

    /* middle */
    public Middle getMiddle() {
        return this.middle;
    }

    public void toRight() {
        getMiddle().toRight();
    }

    public void toLeft() {
        getMiddle().toLeft();
    }

    public void addListener() {
        getMiddle().addListener();
    }

    /* right */
    public Right getRight() {
        return this.right;
    }

    public TreeNode getRoot() {
        return getRight().getRoot();
    }

    public void setRoot(TreeNode root) {
        getRight().setRoot(root);
    }

    public void expandTree() {
        TreeUtils.expandTree(getRight().tree, new TreePath(getRoot()));
    }

    public void collapseTree() {
        TreeUtils.collapseTree(getRight().tree, new TreePath(getRoot()));
    }

    public void expandNode(TreePath path) {
        getRight().tree.expandPath(path);
    }

    public void updateTree() {
        getRight().tree.updateUI();
    }

    public void scrollToTreeNode(List<JsonElement> elements) {
        getRight().scrollToTreeNode(elements);
    }

}
