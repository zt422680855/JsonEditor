package com.jsoneditor.actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:
 * @Author: 19043204
 * @CreateDate: 2020/9/9 20:19
 */
public class CopyToEditor extends AnAction {

    private Project project;

    private ToolWindow toolWindow;

    public CopyToEditor(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext ctx = e.getDataContext();
        Editor editor = ctx.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = ctx.getData(CommonDataKeys.PSI_FILE);
        if (editor != null && psiFile != null) {
//            Project project = editor.getProject();
            PsiElement selectElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
            PsiClass selectClass = PsiTreeUtil.getContextOfType(selectElement, PsiClass.class);
            if (selectClass != null) {
                JSONObject object = generateObj(selectClass);

                toolWindow.show(null);
            }
        }
    }

    private JSONObject generateObj(PsiClass psiClass) {
        JSONObject obj = new JSONObject(true);
        PsiField[] fields = psiClass.getAllFields();
        for (PsiField field : fields) {
            String name = field.getName();
            PsiType type = field.getType();
            String typeName = type.getPresentableText();
            if (type instanceof PsiClassType) {
                PsiClassType classType = (PsiClassType) type;
                String className = classType.getClassName();
                obj.put(name, getObjByClassName(className));
            } else if (type instanceof PsiArrayType) {
                PsiClass containingClass = field.getContainingClass();
                if (containingClass != null) {
                    obj.put(name, new JSONArray() {{
                        add(generateObj(containingClass));
                    }});
                }
            }
        }
        return obj;
    }

    private Object getObjByClassName(String className) {
        try {
            Class<?> aClass = Class.forName(className);
            return aClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private PsiClass getPsiClassByShortClassName(String shortClassName, PsiClass hostClass) {
        PsiJavaFile hostJavaFile = (PsiJavaFile) hostClass.getContainingFile();
        PsiImportList importList = hostJavaFile.getImportList();
        PsiImportStatement[] importStatements;
        if (importList != null) {
            importStatements = importList.getImportStatements();
        } else {
            importStatements = new PsiImportStatement[0];
        }
        Stream<String> qualifiedNames = Arrays.stream(importStatements).map(PsiImportStatement::getQualifiedName);
        PsiClass[] classes = PsiShortNamesCache.getInstance(project)
                .getClassesByName(shortClassName, GlobalSearchScope.projectScope(project));
        for (PsiClass psiClass : classes) {
            PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
            String packageName = javaFile.getPackageName();
            String curQualifiedName = packageName + "." + psiClass.getName();
            Optional<String> target = qualifiedNames.filter(curQualifiedName::equals).findFirst();
            if (target.isPresent()) {
                return psiClass;
            }
        }
        return null;
    }
}
