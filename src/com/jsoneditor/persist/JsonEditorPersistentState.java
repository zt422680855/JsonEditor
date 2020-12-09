package com.jsoneditor.persist;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.jsoneditor.Constant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/12/8 21:55
 */
@State(name = "jsonEditor", storages = {@Storage("$PROJECT_FILE$")})
public class JsonEditorPersistentState implements PersistentStateComponent<JsonEditorPersistentState> {

    /* 需要持久化的属性必须是public */
    public Map<String, String> stateMap;

    public JsonEditorPersistentState() {
        this.stateMap = new ConcurrentHashMap<>();
    }

    @Nullable
    @Override
    public JsonEditorPersistentState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull JsonEditorPersistentState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getText(String key) {
        String text = stateMap.get(key);
        return StringUtil.isEmpty(text) ? Constant.TEMP : text;
    }

    public void setText(String key, String text) {
        stateMap.put(key, text);
    }
}
