package com.jsoneditor.moddles;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import com.jsoneditor.TreeUtils;
import com.jsoneditor.Undo;
import com.jsoneditor.node.TreeNode;
import com.jsoneditor.notification.JsonEditorNotifier;
import icons.Icons;

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

    public JButton syncToRight = new JButton() {{
        setIcon(Icons.TO_RIGHT);
        setBorderPainted(false);
    }};
    public JButton syncToLeft = new JButton() {{
        setIcon(Icons.TO_LEFT);
        setBorderPainted(false);
    }};

    public Middle(JBPanel panel) {
        this.parentPanel = panel;
        this.layout = new GridBagLayout();
        setLayout(this.layout);
        paint();
    }

    private void paint() {
        GridBagLayout parentLayout = (GridBagLayout) parentPanel.getLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 5;
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
                TreeNode root;
                Object parse = JSON.parse(left.textPanel.getText(), Feature.OrderedField);
                root = TreeNode.getNode("ROOT", parse);
                right.setRoot(root);
                TreeUtils.refreshTree(root);
                right.tree.expandPath(new TreePath(root.getPath()));
                right.tree.updateUI();
                Undo.clear();
            } catch (Exception ex) {
                JsonEditorNotifier.error("JSON format error.");
            }
        });
    }

    public void toLeft(Left left, Right right) {
        syncToLeft.addActionListener((e) -> {
            TreeNode root = right.getRoot();
            TreeUtils.refreshJson(root);
            left.textPanel.setText(JSON.toJSONString(root.value, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
        });
    }

}
