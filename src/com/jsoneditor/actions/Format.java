package com.jsoneditor.actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AnActionButton;
import com.jsoneditor.moddles.ModdleContext;
import com.jsoneditor.notification.JsonEditorNotifier;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/21 21:01
 */
public class Format extends AnActionButton {

    public Format() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(Icons.FORMAT);
        presentation.setText("format");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            Project project = e.getProject();
            Object json = JSON.parse(ModdleContext.getText(project), Feature.OrderedField);
            ModdleContext.setText(project, JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
            ModdleContext.resetScrollBarPosition(project);
        } catch (Exception ex) {
            JsonEditorNotifier.error("JSON format error.");
        }
    }
}
