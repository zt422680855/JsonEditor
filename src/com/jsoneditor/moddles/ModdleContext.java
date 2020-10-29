package com.jsoneditor.moddles;

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
    public static void setText(Project project, String text) {
        getContext(project).left.setText(text);
    }

    public static String getText(Project project) {
        return getContext(project).left.getText();
    }

    public static void resetScrollBarPosition(Project project) {
        getContext(project).left.resetScrollBarPosition();
    }

    public static void scrollToText(Project project, List<TreeNode> path) {
        getContext(project).left.scrollToText(path);
    }

    /* middle */
    public static void toRight(Project project) {
        getContext(project).middle.toRight();
    }

    public static void toLeft(Project project) {
        getContext(project).middle.toLeft();
    }

    public static void addListener(Project project) {
        getContext(project).middle.addListener();
    }

    /* right */
    public static TreeNode getRoot(Project project) {
        return getContext(project).right.getRoot();
    }

    public static void setRoot(Project project, TreeNode root) {
        getContext(project).right.setRoot(root);
    }

    public static void expandTree(Project project) {
        TreeUtils.expandTree(getContext(project).right.tree, new TreePath(getRoot(project)));
    }

    public static void collapseTree(Project project) {
        TreeUtils.collapseTree(getContext(project).right.tree, new TreePath(getRoot(project)));
    }

    public static void expandNode(Project project, TreePath path) {
        getContext(project).right.tree.expandPath(path);
    }

    public static void updateTree(Project project) {
        getContext(project).right.tree.updateUI();
    }

}
