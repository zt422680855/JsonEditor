package com.jsoneditor.moddles;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.jsoneditor.Constant;
import com.jsoneditor.Constant.DateFormat;
import com.jsoneditor.Constant.SelectItem;
import com.jsoneditor.Utils;
import com.jsoneditor.node.*;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiConsumer;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 21:37
 */
public class AddOrEdit extends JDialog {

    private Project project;

    private JBLabel typeLabel = new JBLabel("type");

    private ComboBox<SelectItem> type = new ComboBox<SelectItem>() {{
        addItem(SelectItem.String);
        addItem(SelectItem.Object);
        addItem(SelectItem.Array);
        addItem(SelectItem.Date);
        addItem(SelectItem.Other);
    }};

    private JBTextField key = new JBTextField();

    private JBLabel keyLabel = new JBLabel("key");

    private JBTextField value = new JBTextField();

    private JBLabel valueLabel = new JBLabel("value");

    private DatePicker datePicker = new DatePicker() {{
        setDateFormat(new SimpleDateFormat(Constant.DateFormat.FIVE.getFormat()));
        setVisible(false);
    }};

    private JBLabel datePickerLabel = new JBLabel("value") {{
        setVisible(false);
    }};

    private ComboBox<String> dateFormat = new ComboBox<String>() {{
        addItem(Constant.DateFormat.DEFAULT.getFormat());
        addItem(Constant.DateFormat.ONE.getFormat());
        addItem(Constant.DateFormat.TWO.getFormat());
        addItem(Constant.DateFormat.THREE.getFormat());
        addItem(Constant.DateFormat.FOUR.getFormat());
        addItem(Constant.DateFormat.FIVE.getFormat());
        addItem(Constant.DateFormat.SIX.getFormat());
        setVisible(false);
    }};

    private JButton ok = new JButton("ok");

    private JButton cancel = new JButton("cancel");

    private TreeNode selectNode;

    private String title = "Add";

    private BiConsumer<TreeNode, TreeNode> callback;

    public AddOrEdit(Project project, TreeNode node, Integer opt, BiConsumer<TreeNode, TreeNode> callback) {
        this.project = project;
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
            } else if (node instanceof DateNode) {
                type.setSelectedItem(SelectItem.Date);
                value.setVisible(false);
                valueLabel.setVisible(false);
                datePickerLabel.setVisible(true);
                datePicker.setVisible(true);
                dateFormat.setVisible(true);
                DateNode dateNode = (DateNode) node;
                try {
                    datePicker.setDate(dateNode.value);
                    if (!DateFormat.DEFAULT.getFormat().equals(dateNode.format)) {
                        datePicker.setDateFormat(new SimpleDateFormat(dateNode.format));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dateFormat.setSelectedItem(dateNode.format);
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
        value.setText(opt != 3 ? "value" : node.valueString());
        openDialog();
    }

    private void openDialog() {
        setTitle(title);
        setSize(300, 200);
        setLocationRelativeTo(ModdleContext.getParent(project));
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
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = JBUI.insets(0, 0, 5, 15);
        layout.setConstraints(value, c);
        c = new GridBagConstraints();
        c.weightx = 10;
        c.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(datePickerLabel, c);
        c.weightx = 20;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = JBUI.insets(0, 0, 5, 15);
        layout.setConstraints(datePicker, c);
        c.weightx = 20;
        c.gridx = 1;
        c.gridy = 4;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = JBUI.insets(0, 0, 5, 15);
        layout.setConstraints(dateFormat, c);
        c = new GridBagConstraints();
        c.weightx = 10;
        c.gridx = 1;
        c.gridy = 5;
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
        p.add(datePickerLabel);
        p.add(datePicker);
        p.add(dateFormat);
        p.add(ok);
        p.add(cancel);
        addListener();
        setVisible(true);
    }

    private void addListener() {
        type.addItemListener((e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                SelectItem select = (SelectItem) type.getSelectedItem();
                if (SelectItem.Object.equals(select) || SelectItem.Array.equals(select) || SelectItem.Date.equals(select)) {
                    value.setVisible(false);
                    valueLabel.setVisible(false);
                } else {
                    value.setVisible(true);
                    valueLabel.setVisible(true);
                }
                if (SelectItem.Date.equals(select)) {
                    datePicker.setVisible(true);
                    datePickerLabel.setVisible(true);
                    dateFormat.setVisible(true);
                } else {
                    datePicker.setVisible(false);
                    datePickerLabel.setVisible(false);
                    dateFormat.setVisible(false);
                }
            }
        });
        dateFormat.addItemListener((e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String curFormat = (String) dateFormat.getSelectedItem();
                if (curFormat != null && !curFormat.equals(DateFormat.DEFAULT.getFormat())) {
                    datePicker.setDateFormat(new SimpleDateFormat(curFormat));
                }
            }
        });
        ok.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String nodeKey = key.getText();
                TreeNode returnNode;
                SelectItem nodeType = (SelectItem) type.getSelectedItem();
                if (SelectItem.Object.equals(nodeType)) {
                    returnNode = new ObjectNode(nodeKey, new JSONObject(true));
                } else if (SelectItem.Array.equals(nodeType)) {
                    returnNode = new ArrayNode(nodeKey, new JSONArray());
                } else if (SelectItem.String.equals(nodeType)) {
                    returnNode = new StringNode(nodeKey, value.getText());
                } else if (SelectItem.Date.equals(nodeType)) {
                    Date date = datePicker.getDate();
                    String selectedFormat = (String) dateFormat.getSelectedItem();
                    if (DateFormat.DEFAULT.getFormat().equals(selectedFormat)) {
                        returnNode = new DateNode(nodeKey, date);
                    } else {
                        returnNode = new DateNode(nodeKey, date, selectedFormat);
                    }
                } else {
                    Object nodeValue;
                    String valueStr = value.getText();
                    if ("".equals(valueStr)) {
                        nodeValue = null;
                    } else if (Boolean.TRUE.toString().equalsIgnoreCase(valueStr) || Boolean.FALSE.toString().equalsIgnoreCase(valueStr)) {
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
                    if (nodeValue == null) {
                        returnNode = new NullNode(nodeKey);
                    } else {
                        returnNode = new OtherNode(nodeKey, nodeValue);
                    }
                }
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
