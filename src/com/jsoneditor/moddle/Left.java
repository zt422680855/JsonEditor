package com.jsoneditor.moddle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.jsoneditor.Constant;

import javax.swing.*;
import java.awt.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 21:48
 */
public class Left extends JBPanel {

    private JBPanel parentPanel;

    private GridBagLayout layout;

    protected JTextArea textArea = new JTextArea();
    private JButton format = new JButton("format");
    private JButton compressJson = new JButton("compress");
    private JButton reset = new JButton("reset");

    public Left(JBPanel panel) {
        this.parentPanel = panel;
        this.layout = new GridBagLayout();
        setLayout(this.layout);
        paint();
        format();
        compress();
        reset();
    }

    private void paint() {
        GridBagLayout parentLayout = (GridBagLayout) parentPanel.getLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 150;
        c.weighty = 10;
        c.fill = GridBagConstraints.BOTH;
        parentLayout.setConstraints(this, c);
        parentPanel.add(this);
        c = new GridBagConstraints();
        c.weightx = 10;
        c.weighty = 4;
        c.ipady = -10;
        c.fill = GridBagConstraints.BOTH;
        layout.setConstraints(format, c);
        layout.setConstraints(compressJson, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(reset, c);
        add(format);
        add(compressJson);
        add(reset);
        c = new GridBagConstraints();
        c.weighty = 100;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        JBScrollPane scrollPane = new JBScrollPane(textArea);
        textArea.setText(Constant.TEMP);
        scrollPane.setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        layout.setConstraints(scrollPane, c);
        add(scrollPane);
    }

    private void format() {
        format.addActionListener((e) -> {
            try {
                Object json = JSON.parse(textArea.getText(), Feature.OrderedField);
                textArea.setText(JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "JSON format error.",
                        "error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void compress() {
        compressJson.addActionListener((e) -> {
            try {
                Object json = JSON.parse(textArea.getText(), Feature.OrderedField);
                textArea.setText(JSON.toJSONString(json, SerializerFeature.WriteMapNullValue));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "JSON format error.",
                        "error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void reset() {
        reset.addActionListener((e) -> {
            Object json = JSON.parse(Constant.TEMP, Feature.OrderedField);
            textArea.setText(JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
        });
    }

}
