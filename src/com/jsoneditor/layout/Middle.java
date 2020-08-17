package com.jsoneditor.layout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import com.jsoneditor.TreeUtils;
import com.jsoneditor.Undo;
import com.jsoneditor.node.ObjectNode;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 21:51
 */
public class Middle extends JBPanel {

    private JBPanel parentPanel;

    private GridBagLayout layout;

    public JButton syncToRight = new JButton(">");
    public JButton syncToLeft = new JButton("<");

    public Middle(JBPanel panel) {
        this.parentPanel = panel;
        this.layout = new GridBagLayout();
        setLayout(this.layout);
        paint();
    }

    private void paint() {
        GridBagLayout parentLayout = (GridBagLayout) parentPanel.getLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 10;
        c.fill = GridBagConstraints.BOTH;
        parentLayout.setConstraints(this, c);
        parentPanel.add(this);
        c = new GridBagConstraints();
        c.ipadx = -35;
        c.ipady = -1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = JBUI.insets(10, 0, 10, 0);
        layout.setConstraints(syncToRight, c);
        layout.setConstraints(syncToLeft, c);
        add(syncToRight);
        add(syncToLeft);
    }

    public void toRight(Left left, Right right) {
        syncToRight.addActionListener((e) -> {
            try {
                ObjectNode root = right.root;
                right.root.value = JSON.parse(left.textArea.getText(), Feature.OrderedField);
                TreeUtils.refreshTree(root);
                right.tree.expandPath(new TreePath(root.getPath()));
                right.tree.updateUI();
                Undo.clear();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "JSON format error.",
                        "error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void toLeft(Left left, Right right) {
        syncToLeft.addActionListener((e) -> {
            ObjectNode root = right.root;
            TreeUtils.refreshJson(root);
            left.textArea.setText(JSON.toJSONString(root.value, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
        });
    }

}