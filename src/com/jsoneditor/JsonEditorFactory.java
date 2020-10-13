package com.jsoneditor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManagerAdapter;
import com.intellij.ui.content.ContentManagerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/8 21:38
 */
public class JsonEditorFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JsonEditorWindow jsonEditor = new JsonEditorWindow(project, toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(jsonEditor, project.getName(), false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.getContentManager().addContentManagerListener(new ContentManagerAdapter() {

            @Override
            public void selectionChanged(@NotNull ContentManagerEvent event) {
                if (event.getOperation().equals(ContentManagerEvent.ContentOperation.add)) {
                    jsonEditor.setContext();
                }
            }
        });
    }
}
