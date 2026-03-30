package com.jsoneditor.moddles;

import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;

import java.awt.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 21:48
 */
public class Left extends JsonEditorModdle {

    public TextPanel textPanel;

    public Left(Project project, JsonEditorModdle parent) {
        super(project, parent);
        paint();
    }

    private void paint() {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        textPanel = new TextPanel(project);
        layout.setConstraints(textPanel, c);
        add(textPanel);
    }

    public EditorEx getEditor() {
        return this.textPanel.getEditor();
    }

}
