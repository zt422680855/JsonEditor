package com.jsoneditor.actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.content.Content;
import com.jsoneditor.JsonEditorWindow;
import com.jsoneditor.notification.JsonEditorNotifier;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/9 22:19
 */
public class CopyToEditor extends AnAction {

    public CopyToEditor() {

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext ctx = e.getDataContext();
        Editor editor = ctx.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = ctx.getData(CommonDataKeys.PSI_FILE);
        if (editor != null && psiFile != null) {
            Project project = editor.getProject();
            if (project != null) {
                ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
                ToolWindow toolWindow = toolWindowManager.getToolWindow("JsonEditor");
                PsiElement selectElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
                PsiClass containingClass = PsiTreeUtil.getContextOfType(selectElement, PsiClass.class);
                if (selectElement != null && containingClass != null) {
                    String selectText = selectElement.getText();
                    PsiClass selectClass;
                    if (selectText.equals(containingClass.getName())) {
                        selectClass = containingClass;
                    } else {
                        selectClass = getPsiClassByShortClassName(selectText, containingClass, project);
                    }
                    if (selectClass != null) {
                        JSONObject object = generateObj(selectClass, project);
                        Content[] contents = toolWindow.getContentManager().getContents();
                        Arrays.stream(contents).filter(c -> "JsonEditor".equals(c.getDisplayName())).findAny().ifPresent(content -> {
                            JComponent component = content.getComponent();
                            if (component instanceof JsonEditorWindow) {
                                JsonEditorWindow window = (JsonEditorWindow) component;
                                window.setText(JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
                                window.toRight();
                                toolWindow.show(null);
                            }
                        });
                    } else {
                        JsonEditorNotifier.hintError(editor, selectText + " is not a class or is not a project class.");
                    }
                }
            }
        }
    }

    private JSONObject generateObj(PsiClass psiClass, Project project) {
        JSONObject obj = new JSONObject(true);
        if (psiClass != null) {
            PsiField[] fields = psiClass.getFields();
            for (PsiField field : fields) {
                String name = field.getName();
                PsiType type = field.getType();
                String typeName = type.getPresentableText();
                if (type instanceof PsiClassType) {
                    PsiClassType classType = (PsiClassType) type;
                    if (qualifiedCheck(classType, "java.lang.String", project)) {
                        obj.put(name, "");
                    } else if (qualifiedCheck(classType, "java.util.Date", project)) {
                        obj.put(name, System.currentTimeMillis());
                    } else if (qualifiedCheck(classType, "java.lang.Integer", project) ||
                            qualifiedCheck(classType, "java.lang.Long", project) ||
                            qualifiedCheck(classType, "java.lang.Short", project) ||
                            qualifiedCheck(classType, "java.lang.Byte", project)) {
                        obj.put(name, 0);
                    } else if (qualifiedCheck(classType, "java.lang.Boolean", project)) {
                        obj.put(name, true);
                    } else if (qualifiedCheck(classType, "java.lang.Double", project) ||
                            qualifiedCheck(classType, "java.lang.Float", project)) {
                        obj.put(name, 0.0);
                    } else if (ancestorQualifiedCheck(classType, "java.util.Collection", project)) {
                        JSONArray arr = new JSONArray();
                        PsiType[] parameters = classType.getParameters();
                        if (parameters.length == 1) {
                            PsiType genericType = parameters[0];
                            if (genericType instanceof PsiClassType) {
                                PsiClassType genericClassType = (PsiClassType) genericType;
                                PsiClass fieldClass = getPsiClassByShortClassName(genericClassType.getName(), psiClass, project);
                                if (!psiClass.equals(fieldClass)) {
                                    arr.add(generateObj(fieldClass, project));
                                }
                            }
                        }
                        obj.put(name, arr);
                    } else if (ancestorQualifiedCheck(classType, "java.util.Map", project)) {
                        obj.put(name, new JSONObject());
                    } else {
                        String className = classType.getClassName();
                        PsiClass fieldClass = getPsiClassByShortClassName(className, psiClass, project);
                        if (!psiClass.equals(fieldClass)) {
                            obj.put(name, generateObj(fieldClass, project));
                        } else {
                            obj.put(name, typeName);
                        }
                    }
                } else if (type instanceof PsiArrayType) {
                    JSONArray arr = new JSONArray();
                    PsiArrayType arrayType = (PsiArrayType) type;
                    PsiType arrayElementType = arrayType.getComponentType();
                    if (arrayElementType instanceof PsiClassType) {
                        PsiClassType elementClassType = (PsiClassType) arrayElementType;
                        PsiClass fieldClass = getPsiClassByShortClassName(elementClassType.getName(), psiClass, project);
                        if (fieldClass != null) {
                            if (!psiClass.equals(fieldClass)) {
                                arr.add(generateObj(fieldClass, project));
                            }
                        }
                    }
                    obj.put(name, arr);
                } else if (type instanceof PsiPrimitiveType) {
                    Object value;
                    if (PsiType.INT.equals(type) || PsiType.LONG.equals(type) ||
                            PsiType.SHORT.equals(type) || PsiType.BYTE.equals(type)) {
                        value = 0;
                    } else if (type.equals(PsiType.BOOLEAN)) {
                        value = true;
                    } else if (PsiType.DOUBLE.equals(type) || PsiType.FLOAT.equals(type)) {
                        value = 0.0;
                    } else {
                        value = typeName;
                    }
                    obj.put(name, value);
                }
            }
        }
        return obj;
    }

    private boolean ancestorQualifiedCheck(PsiType type, String ancestorQualifiedName, Project project) {
        if (qualifiedCheck(type, ancestorQualifiedName, project)) {
            return true;
        }
        PsiType[] superTypes = type.getSuperTypes();
        if (superTypes.length > 0) {
            return Arrays.stream(type.getSuperTypes()).anyMatch(t -> ancestorQualifiedCheck(t, ancestorQualifiedName, project));
        }
        return false;
    }

    private boolean qualifiedCheck(PsiType type, String qualifiedName, Project project) {
        if (type instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) type;
            String name = classType.getName();
            PsiClass[] classes = PsiShortNamesCache.getInstance(project)
                    .getClassesByName(name, GlobalSearchScope.allScope(project));
            return Arrays.stream(classes).anyMatch(cls -> qualifiedName.equals(cls.getQualifiedName()));
        } else {
            return false;
        }
    }

    private PsiClass getPsiClassByShortClassName(String shortClassName, PsiClass containingClass, Project project) {
        PsiClass[] classes = PsiShortNamesCache.getInstance(project)
                .getClassesByName(shortClassName, GlobalSearchScope.projectScope(project));
        if (classes.length == 0) {
            return null;
        } else if (classes.length == 1) {
            return classes[0];
        } else {
            PsiJavaFile hostJavaFile = (PsiJavaFile) containingClass.getContainingFile();
            PsiImportList importList = hostJavaFile.getImportList();
            PsiImportStatement[] importStatements;
            if (importList != null) {
                importStatements = importList.getImportStatements();
            } else {
                importStatements = new PsiImportStatement[0];
            }
            Set<String> qualifiedNames = Arrays.stream(importStatements)
                    .map(PsiImportStatement::getQualifiedName).collect(Collectors.toSet());
            return Arrays.stream(classes).filter(cls -> {
                String qualifiedName = cls.getQualifiedName();
                return qualifiedNames.contains(qualifiedName);
            }).findFirst().orElse(null);
        }
    }
}
