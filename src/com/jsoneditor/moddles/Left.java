package com.jsoneditor.moddles;

import com.intellij.openapi.project.Project;
import com.jsoneditor.node.TreeNode;

import java.awt.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 21:48
 */
public class Left extends JsonEditorModdle {

    private JsonEditorModdle parent;

    private TextPanel textPanel;

    public Left(Project project, JsonEditorModdle parent) {
        super(project);
        this.parent = parent;
        paint();
    }

    private void paint() {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagLayout parentLayout = (GridBagLayout) parent.getLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 100;
        c.weighty = 205;
        c.fill = GridBagConstraints.BOTH;
        parentLayout.setConstraints(this, c);
        parent.add(this);
        textPanel = new TextPanel(project);
        layout.setConstraints(textPanel, c);
        add(textPanel);
    }

    public void setText(String text) {
        textPanel.setText(text);
    }

    public String getText() {
        return textPanel.getText();
    }

    public void resetScrollBarPosition() {
        textPanel.resetScrollBarPosition();
    }

    public void scrollToText(java.util.List<TreeNode> path) {
        try {
            textPanel.scrollToText(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
