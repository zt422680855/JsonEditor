package com.jsoneditor.moddles;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/28 21:46
 */
public class JsonEditorModdle extends JBPanel {

    protected Project project;

    protected JsonEditorModdle parent;

    public JsonEditorModdle(Project project, JsonEditorModdle parent) {
        this.project = project;
        this.parent = parent;
    }

    public JsonEditorModdle(Project project) {
        this(project, null);
    }

}
