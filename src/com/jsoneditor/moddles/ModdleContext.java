package com.jsoneditor.moddles;

import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.jsoneditor.JsonEditorWindow;
import com.jsoneditor.TreeUtils;
import com.jsoneditor.node.TreeNode;

import javax.swing.tree.TreePath;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/28 21:42
 */
public class ModdleContext {

    private static Map<Project, ModdleContext> contextMap = new ConcurrentHashMap<>();

    private JsonEditorWindow parent;

    private Left left;

    private Middle middle;

    private Right right;

    public static void addModdle(Project project, JsonEditorModdle... moddles) {
        ModdleContext ctx = getContext(project);
        if (ctx == null) {
            ctx = new ModdleContext();
            contextMap.put(project, ctx);
        }
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

    public static ModdleContext getContext(Project project) {
        return contextMap.get(project);
    }

    public static JsonEditorWindow getParent(Project project) {
        return getContext(project).parent;
    }

    /* left */
    public static Left getLeft(Project project) {
        return getContext(project).left;
    }

    public static EditorEx getEditor(Project project) {
        return getLeft(project).getEditor();
    }

    public static void setText(Project project, String text) {
        getLeft(project).setText(text);
    }

    public static String getText(Project project) {
        return getLeft(project).getText();
    }

    public static void resetScrollBarPosition(Project project) {
        getLeft(project).resetScrollBarPosition();
    }

    public static void scrollToText(Project project, List<TreeNode> path) {
        getLeft(project).scrollToText(path);
    }

    /* middle */
    public static Middle getMiddle(Project project) {
        return getContext(project).middle;
    }

    public static void toRight(Project project) {
        getMiddle(project).toRight();
    }

    public static void toLeft(Project project) {
        getMiddle(project).toLeft();
    }

    public static void addListener(Project project) {
        getMiddle(project).addListener();
    }

    /* right */
    public static Right getRight(Project project) {
        return getContext(project).right;
    }

    public static TreeNode getRoot(Project project) {
        return getRight(project).getRoot();
    }

    public static void setRoot(Project project, TreeNode root) {
        getRight(project).setRoot(root);
    }

    public static void expandTree(Project project) {
        TreeUtils.expandTree(getRight(project).tree, new TreePath(getRoot(project)));
    }

    public static void collapseTree(Project project) {
        TreeUtils.collapseTree(getRight(project).tree, new TreePath(getRoot(project)));
    }

    public static void expandNode(Project project, TreePath path) {
        getRight(project).tree.expandPath(path);
    }

    public static void updateTree(Project project) {
        getRight(project).tree.updateUI();
    }

}
