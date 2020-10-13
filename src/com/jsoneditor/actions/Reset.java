package com.jsoneditor.actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.ui.AnActionButton;
import com.jsoneditor.Constant;
import com.jsoneditor.moddles.Left;
import com.jsoneditor.moddles.ModdleContext;
import org.jetbrains.annotations.NotNull;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/21 22:12
 */
public class Reset extends AnActionButton {

    public Reset() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.Rollback);
        presentation.setText("reset");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        Object json = JSON.parse(Constant.TEMP, Feature.OrderedField);
        ModdleContext.setText(JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
    }
}
