package com.jsoneditor.actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.ui.AnActionButton;
import com.jsoneditor.moddles.Left;
import com.jsoneditor.notification.JsonEditorNotifier;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/21 22:44
 */
public class Compress extends AnActionButton {

    private Left left;

    public Compress(Left left) {
        this.left = left;
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(Icons.COMPRESS);
        presentation.setText("compress");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        try {
            Object json = JSON.parse(left.getText(), Feature.OrderedField);
            left.setText(JSON.toJSONString(json, SerializerFeature.WriteMapNullValue));
        } catch (Exception ex) {
            JsonEditorNotifier.error("JSON format error.");
        }
    }
}
