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
 * @Description: 压缩json
 * @Author: zhengt
 * @CreateDate: 2020/8/21 22:44
 */
public class Compress extends AnActionButton {

    public Compress() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(Icons.COMPRESS);
        presentation.setText("compress");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            Project project = e.getProject();
            Object json = JSON.parse(ModdleContext.getText(project), Feature.OrderedField);
            ModdleContext.setText(project, JSON.toJSONString(json, SerializerFeature.WriteMapNullValue));
        } catch (Exception ex) {
            JsonEditorNotifier.error("JSON format error.");
        }
    }
}
