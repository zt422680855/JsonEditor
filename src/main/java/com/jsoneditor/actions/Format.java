package com.jsoneditor.actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.ex.EditorEx;
import com.jsoneditor.JsonEditorWindow;
import com.jsoneditor.notification.JsonEditorNotifier;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/21 21:01
 */
public class Format extends BaseAction {

    // 0 fastjson format, 1 idea默认格式化
    private Integer state;

    public Format(JsonEditorWindow jsonEditor) {
        super(jsonEditor);
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(Icons.FORMAT);
        presentation.setText("format");
        this.state = 0;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            EditorEx editor = getCtx().getEditor();
            editor.getSelectionModel().removeSelection();
            if (state == 0) {
                // fastjson格式化
                Object json = JSON.parse(getCtx().getText(), Feature.OrderedField);
                // 将格式化好的文本赋值给editor
                getCtx().setText(JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
                // 滚动条滚动至初始位置
                state = 1;
            } else {
                // idea默认格式化
                getCtx().formatCode();
                state = 0;
            }
            getCtx().resetScrollBarPosition();
        } catch (Exception ex) {
            JsonEditorNotifier.error("JSON format error.");
        }
    }

}
