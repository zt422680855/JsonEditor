package com.jsoneditor.moddles;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 21:48
 */
public class Left extends JBPanel {

    private GridBagLayout layout;

    private Project project;

    private TextPanel textPanel;

    public Left(Project project) {
        this.project = project;
        this.layout = new GridBagLayout();
        setLayout(this.layout);
        paint();
    }

    private void paint() {
        textPanel = new TextPanel(project);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        layout.setConstraints(textPanel, c);
        add(textPanel);
    }

    public void setText(String text) {
        textPanel.setText(text);
    }

    public String getText() {
        return textPanel.getText();
    }

}
