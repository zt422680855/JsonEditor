package com.jsoneditor.moddles;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;

import java.awt.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 21:48
 */
public class Left extends JBPanel {

    private JBPanel parentPanel;

    private GridBagLayout layout;

    private Project project;

    public TextPanel textPanel;

    public Left(JBPanel panel, Project project) {
        this.project = project;
        this.parentPanel = panel;
        this.layout = new GridBagLayout();
        setLayout(this.layout);
        paint();
    }

    private void paint() {
        GridBagLayout parentLayout = (GridBagLayout) parentPanel.getLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 100;
        c.weighty = 205;
        c.fill = GridBagConstraints.BOTH;
        parentLayout.setConstraints(this, c);
        parentPanel.add(this);
        textPanel = new TextPanel(project);
        layout.setConstraints(textPanel, c);
        add(textPanel);
    }

}
