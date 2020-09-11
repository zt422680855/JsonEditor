package com.jsoneditor.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @Description:
 * @Author: 19043204
 * @CreateDate: 2020/9/9 20:19
 */
public class CopyToEditor extends AnAction {

    private ToolWindow toolWindow;

    public CopyToEditor(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext ctx = e.getDataContext();
        Editor editor = ctx.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = ctx.getData(CommonDataKeys.PSI_FILE);
        if (editor != null && psiFile != null) {
            Project project = editor.getProject();
            PsiElement selectElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
            PsiClass selectClass = PsiTreeUtil.getContextOfType(selectElement, PsiClass.class);
            if (selectClass != null) {
                PsiField[] fields = selectClass.getAllFields();

                toolWindow.show(null);
            }
        }
    }
}
