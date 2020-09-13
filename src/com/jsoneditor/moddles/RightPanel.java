package com.jsoneditor.moddles;

import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

/**
 * @Description:
 * @Author: 19043204
 * @CreateDate: 2020/9/13 12:31
 */
public class RightPanel extends JBPanel {

    private GridBagLayout layout;

    public Middle middle;

    public Right right;

    public RightPanel() {
        this.middle = new Middle();
        this.right = new Right();
        this.layout = new GridBagLayout();
        setLayout(this.layout);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 10;
        layout.setConstraints(middle, c);
        add(middle);
        c.weightx = 100;
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(right, c);
        add(right);
    }

}
