package com.jsoneditor.buttons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.jsoneditor.moddle.Left;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/21 21:01
 */
public class Format extends AnAction {

    private Left left;

    public Format(Left left) {
        this.left = left;
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(Icons.FORMAT);
        presentation.setText("format");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        try {
            Object json = JSON.parse(left.textArea.getText(), Feature.OrderedField);
            left.textArea.setText(JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(left.getParent(), "JSON format error.",
                    "error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
