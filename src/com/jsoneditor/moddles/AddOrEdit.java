package com.jsoneditor.moddles;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

import com.jsoneditor.Constant.SelectItem;
import com.jsoneditor.Utils;
import com.jsoneditor.node.ArrayNode;
import com.jsoneditor.node.ObjectNode;
import com.jsoneditor.node.StringNode;
import com.jsoneditor.node.TreeNode;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 21:37
 */
public class AddOrEdit extends JDialog {

    private Container container;

    private JBLabel typeLabel = new JBLabel("type");

    private ComboBox<SelectItem> type = new ComboBox<SelectItem>() {{
        addItem(SelectItem.String);
        addItem(SelectItem.Object);
        addItem(SelectItem.Array);
        addItem(SelectItem.Other);
    }};

    private JBTextField key = new JBTextField();

    private JBLabel keyLabel = new JBLabel("key");

    private JBTextField value = new JBTextField();

    private JBLabel valueLabel = new JBLabel("value");

    private JButton ok = new JButton("ok");

    private JButton cancel = new JButton("cancel");

    private TreeNode selectNode;

    private String title = "Add";

    private BiConsumer<TreeNode, TreeNode> callback;

    public AddOrEdit(Container container, TreeNode node, Integer opt, BiConsumer<TreeNode, TreeNode> callback) {
        this.container = container;
        this.selectNode = node;
        this.callback = callback;
        // 1、2、3分别代表新增子节点、新增兄弟节点、编辑节点
        if (opt == 1) {
            type.setSelectedItem(SelectItem.String);
            if (node instanceof ArrayNode) {
                key.setVisible(false);
                keyLabel.setVisible(false);
            }
        } else if (opt == 2) {
            type.setSelectedItem(SelectItem.String);
            TreeNode parent = node.getParent();
            if (parent instanceof ArrayNode) {
                key.setVisible(false);
                keyLabel.setVisible(false);
            }
        } else if (opt == 3) {
            title = "Edit";
            if (node instanceof ObjectNode) {
                type.setSelectedItem(SelectItem.Object);
                value.setVisible(false);
                valueLabel.setVisible(false);
            } else if (node instanceof ArrayNode) {
                type.setSelectedItem(SelectItem.Array);
                value.setVisible(false);
                valueLabel.setVisible(false);
            } else if (node instanceof StringNode) {
                type.setSelectedItem(SelectItem.String);
            } else {
                type.setSelectedItem(SelectItem.Other);
            }
            TreeNode parent = selectNode.getParent();
            if (parent instanceof ArrayNode) {
                key.setVisible(false);
                keyLabel.setVisible(false);
            }
        }
        key.setText(opt != 3 ? "key" : node.key);
        value.setText(opt != 3 ? "value" : (node.value == null ? "null" : node.value.toString()));
        openDialog();
    }

    private void openDialog() {
        setTitle(title);
        setSize(300, 180);
        setLocationRelativeTo(container);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setModal(true);
        JBPanel p = new JBPanel();
        add(p);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 10;
        c.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(typeLabel, c);
        c.weightx = 20;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = JBUI.insets(0, 0, 5, 15);
        layout.setConstraints(type, c);
        c = new GridBagConstraints();
        c.weightx = 10;
        c.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(keyLabel, c);
        c.weightx = 20;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = JBUI.insets(0, 0, 5, 15);
        layout.setConstraints(key, c);
        c = new GridBagConstraints();
        c.weightx = 10;
        c.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(valueLabel, c);
        c.weightx = 20;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = JBUI.insets(0, 0, 5, 15);
        layout.setConstraints(value, c);
        c = new GridBagConstraints();
        c.weightx = 30;
        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.EAST;
        c.insets = JBUI.insets(10, 0, 5, 0);
        layout.setConstraints(ok, c);
        c.gridx = 2;
        c.weightx = 5;
        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = JBUI.insets(10, 5, 5, 15);
        layout.setConstraints(cancel, c);
        p.setLayout(layout);
        p.add(typeLabel);
        p.add(type);
        p.add(keyLabel);
        p.add(key);
        p.add(valueLabel);
        p.add(value);
        p.add(ok);
        p.add(cancel);
        addAction();
        setVisible(true);
    }

    private void addAction() {
        type.addItemListener((e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                SelectItem select = (SelectItem) type.getSelectedItem();
                if (SelectItem.Object.equals(select) || SelectItem.Array.equals(select)) {
                    value.setVisible(false);
                    valueLabel.setVisible(false);
                } else {
                    value.setVisible(true);
                    valueLabel.setVisible(true);
                }
            }
        });
        ok.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String nodeKey = key.getText();
                Object nodeValue;
                SelectItem nodeType = (SelectItem) type.getSelectedItem();
                if (SelectItem.Object.equals(nodeType)) {
                    nodeValue = new JSONObject(true);
                } else if (SelectItem.Array.equals(nodeType)) {
                    nodeValue = new JSONArray();
                } else if (SelectItem.String.equals(nodeType)) {
                    nodeValue = value.getText();
                } else {
                    String valueStr = value.getText();
                    if ("".equals(valueStr)) {
                        nodeValue = null;
                    } else if ("true".equalsIgnoreCase(valueStr) || "false".equalsIgnoreCase(valueStr)) {
                        nodeValue = Boolean.parseBoolean(valueStr);
                    } else if (Utils.isInteger(valueStr)) {
                        nodeValue = Long.parseLong(valueStr);
                    } else if (Utils.isFloat(valueStr)) {
                        nodeValue = Double.parseDouble(valueStr);
                    } else if ("null".equals(valueStr)) {
                        nodeValue = null;
                    } else {
                        nodeValue = value.getText();
                    }
                }
                TreeNode returnNode = TreeNode.getNode(nodeKey, nodeValue);
                callback.accept(returnNode, selectNode);
                AddOrEdit.this.dispose();
            }
        });
        cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AddOrEdit.this.dispose();
            }
        });
    }
}
