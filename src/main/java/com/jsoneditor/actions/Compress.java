package com.jsoneditor.actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.jsoneditor.moddles.ModdleContext;
import com.jsoneditor.notification.JsonEditorNotifier;
import org.jetbrains.annotations.NotNull;

/**
 * @Description: 压缩json
 * @Author: zhengt
 * @CreateDate: 2020/8/21 22:44
 */
public class Compress extends AnAction {

    public Compress() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.Collapseall);
        presentation.setText("compress");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            Object json = JSON.parse(ModdleContext.getText(), Feature.OrderedField);
            ModdleContext.setText(JSON.toJSONString(json, SerializerFeature.WriteMapNullValue));
        } catch (Exception ex) {
            JsonEditorNotifier.error("JSON format error.");
        }
    }
}
