package com.jsoneditor.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.jsoneditor.JsonEditorWindow;
import com.jsoneditor.moddles.ModdleContext;

/**
 * @Description: java类作用描述
 * @Author: zhengtao
 * @CreateDate: 2026/3/30 18:19
 */
public abstract class BaseAction extends AnAction {

    private String key;

    private JsonEditorWindow jsonEditorWindow;

    public BaseAction(JsonEditorWindow jsonEditorWindow) {
        this.jsonEditorWindow = jsonEditorWindow;
        this.key = jsonEditorWindow.getTitle();
    }

    public String getKey() {
        return key;
    }

    public ModdleContext getCtx() {
        return jsonEditorWindow.getCtx();
    }

}
