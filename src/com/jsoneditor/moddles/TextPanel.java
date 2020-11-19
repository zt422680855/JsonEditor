package com.jsoneditor.moddles;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.json.JsonFileType;
import com.intellij.json.psi.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.ScrollingModelEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.util.LocalTimeCounter;
import com.jsoneditor.Constant;
import com.jsoneditor.node.ArrayNode;
import com.jsoneditor.node.ObjectNode;
import com.jsoneditor.node.TreeNode;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 文档实例被对应的虚拟文件实例微弱的引用。因此，如果不再被引用，一篇未修改的文档实例可以被垃圾回收，
 * 而如果文档内容之后又再次被访问，新的实例将被创建。将文档引用保存在你的插件的持久数据结构中将导致内存泄漏。
 * https://blog.csdn.net/hawkdowen/article/details/41047229
 * @Author: zhengt
 * @CreateDate: 2020/8/15 21:58
 */
public class TextPanel extends NonOpaquePanel {

    private Project project;

    private PsiFile psiFile;

    private EditorEx editor;

    public TextPanel(Project project) {
        this.project = project;
        JsonFileType fileType = JsonFileType.INSTANCE;
        PsiFileFactory factory = PsiFileFactory.getInstance(project);
        this.psiFile = factory.createFileFromText("JSON." + fileType.getDefaultExtension(),
                fileType, "", LocalTimeCounter.currentTime(), true, false);
        DaemonCodeAnalyzer.getInstance(project).setHighlightingEnabled(psiFile, true);
        // document只能在方法栈中使用，随着方法结束，对象会被回收。不能保存在成员变量、静态变量，否则内存泄露
        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        this.editor = (EditorEx) EditorFactory.getInstance().createEditor(Objects.requireNonNull(document), project);
        EditorHighlighterFactory highlighterFactory = EditorHighlighterFactory.getInstance();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        EditorHighlighter highlighter = highlighterFactory.createEditorHighlighter(this.project, Objects.requireNonNull(virtualFile));
        editor.setHighlighter(highlighter);
        editorSettings(editor);
        editor.setCaretEnabled(true);
        editor.getCaretModel().moveToOffset(document.getTextLength());
        this.add(editor.getComponent());
        setText(Constant.TEMP);
    }

    private void editorSettings(EditorEx editor) {
        editor.setHorizontalScrollbarVisible(true);
        editor.setVerticalScrollbarVisible(true);
        EditorSettings settings = editor.getSettings();
        settings.setAdditionalLinesCount(0);
        settings.setAdditionalColumnsCount(1);
        settings.setRightMarginShown(false);
        settings.setFoldingOutlineShown(true);
        settings.setLineNumbersShown(true);
        settings.setLineMarkerAreaShown(true);
        settings.setIndentGuidesShown(true);
        settings.setVirtualSpace(false);
        settings.setWheelFontChangeEnabled(false);
        settings.setAdditionalPageAtBottom(false);
        settings.setLineCursorWidth(1);
    }

    public EditorEx getEditor() {
        return this.editor;
    }

    public String getText() {
        return editor.getDocument().getText();
    }

    public void setText(String text) {
        executeCommand(() -> ApplicationManager.getApplication().runWriteAction(() -> this.editor.getDocument().setText(text)));
    }

    private void executeCommand(Runnable command) {
        CommandProcessor.getInstance().executeCommand(
                this.project,
                command,
                null,
                null,
                UndoConfirmationPolicy.DEFAULT,
                editor.getDocument()
        );
    }

    public void resetScrollBarPosition() {
        ScrollingModelEx scrollingModel = editor.getScrollingModel();
        scrollingModel.scroll(0, 0);
    }

    public void scrollToText(List<TreeNode> path) {
        PsiElement[] wholeJson = psiFile.getChildren();
        Arrays.stream(wholeJson).filter(ele ->
                StringUtils.isNotBlank(ele.getText()) && ele instanceof JsonContainer
        ).findFirst().ifPresent(first -> {
            JsonValue jsonValue = (JsonValue) first;
            if (path.size() == 1) {
                scrollToSelection(jsonValue.getTextRange());
            } else {
                for (Iterator<TreeNode> it = path.iterator(); it.hasNext(); ) {
                    TreeNode node = it.next();
                    if (node.isRoot()) {
                        continue;
                    }
                    TreeNode parent = node.getParent();
                    int index = parent.getIndex(node);
                    if (jsonValue != null) {
                        if (parent instanceof ObjectNode && jsonValue instanceof JsonObject) {
                            JsonObject obj = (JsonObject) jsonValue;
                            List<JsonProperty> propertyList = obj.getPropertyList();
                            JsonProperty property = propertyList.stream().filter(p -> node.key.equals(p.getName())).findFirst().orElse(null);
                            if (property != null) {
                                jsonValue = property.getValue();
                                if (!it.hasNext()) {
                                    scrollToSelection(property.getTextRange());
                                }
                            }
                        } else if (parent instanceof ArrayNode && jsonValue instanceof JsonArray) {
                            JsonArray arr = (JsonArray) jsonValue;
                            List<JsonValue> valueList = arr.getValueList();
                            if (valueList.size() > index) {
                                jsonValue = valueList.get(index);
                                if (!it.hasNext()) {
                                    scrollToSelection(jsonValue.getTextRange());
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void scrollToSelection(TextRange range) {
        SelectionModel selectionModel = editor.getSelectionModel();
        selectionModel.setSelection(range.getStartOffset(), range.getEndOffset());
        ScrollingModel scrollingModel = editor.getScrollingModel();
        CaretModel caretModel = editor.getCaretModel();
        caretModel.moveToOffset(range.getStartOffset());
        scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE);
    }
}
