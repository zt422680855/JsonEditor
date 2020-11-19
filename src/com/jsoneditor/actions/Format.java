package com.jsoneditor.actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AnActionButton;
import com.jsoneditor.moddles.ModdleContext;
import com.jsoneditor.notification.JsonEditorNotifier;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/21 21:01
 */
public class Format extends AnActionButton {

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
                ModdleContext.resetScrollBarPosition(project);
                state = 1;
            } else {
                // idea默认格式化
                // 首先需要使editor对象获得焦点，不然格式化不起作用
                editor.getContentComponent().requestFocus();
                editor.getCaretModel().moveToOffset(0);
                defaultFormat();
                state = 0;
            }
        } catch (Exception ex) {
            JsonEditorNotifier.error("JSON format error.");
        }
    }

    private void defaultFormat() {
        if (r != null) {
            // 调用机器人，执行ctrl + alt + L格式化
            r.keyPress(KeyEvent.VK_CONTROL);
            r.keyPress(KeyEvent.VK_ALT);
            r.keyPress(KeyEvent.VK_L);
            r.keyRelease(KeyEvent.VK_L);
            r.keyRelease(KeyEvent.VK_ALT);
            r.keyRelease(KeyEvent.VK_CONTROL);
        }
    }
}
