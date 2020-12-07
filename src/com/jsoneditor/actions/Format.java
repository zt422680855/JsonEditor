package com.jsoneditor.actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.jsoneditor.moddles.ModdleContext;
import com.jsoneditor.notification.JsonEditorNotifier;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/21 21:01
 */
public class Format extends AnAction {

    // 0 fastjson format, 1 ctrl + alt + L
    private Integer state;

    private Robot r;

    public Format() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(Icons.FORMAT);
        presentation.setText("format");
        this.state = 0;
        try {
            r = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            Project project = e.getProject();
            EditorEx editor = ModdleContext.getEditor(project);
            editor.getSelectionModel().removeSelection();
            if (state == 0) {
                // fastjson格式化
                Object json = JSON.parse(ModdleContext.getText(project), Feature.OrderedField);
                // 将格式化好的文本赋值给editor
                ModdleContext.setText(project, JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
                // 滚动条滚动至初始位置
                state = 1;
            } else {
                // idea默认格式化
                ModdleContext.formatCode(project);
                state = 0;
            }
            ModdleContext.resetScrollBarPosition(project);
        } catch (Exception ex) {
            JsonEditorNotifier.error("JSON format error.");
        }
    }

}
