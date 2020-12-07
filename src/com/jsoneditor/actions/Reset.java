package com.jsoneditor.actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.jsoneditor.Constant;
import com.jsoneditor.moddles.ModdleContext;
import org.jetbrains.annotations.NotNull;

/**
 * @Description: 重置文本
 * @Author: zhengt
 * @CreateDate: 2020/8/21 22:12
 */
public class Reset extends AnAction {

    public Reset() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.Rollback);
        presentation.setText("reset");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Object json = JSON.parse(Constant.TEMP, Feature.OrderedField);
        ModdleContext.setText(project, JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
    }
}
