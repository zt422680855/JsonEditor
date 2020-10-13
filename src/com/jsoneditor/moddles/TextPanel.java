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

import java.util.Iterator;
import java.util.List;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/15 21:58
 */
public class TextPanel extends NonOpaquePanel {

    private Document document;

    private Project project;

    private PsiFile psiFile;

    private EditorEx editor;

    public TextPanel(Project project) {
        this.project = project;
        JsonFileType fileType = JsonFileType.INSTANCE;
        PsiFileFactory factory = PsiFileFactory.getInstance(project);
        this.psiFile = factory.createFileFromText("JSON." + fileType.getDefaultExtension(),
                fileType, "", LocalTimeCounter.currentTime(), true, false);
        DaemonCodeAnalyzer.getInstance(project).setHighlightingEnabled(psiFile, false);
        this.document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        EditorFactory editorFactory = EditorFactory.getInstance();
        this.editor = (EditorEx) editorFactory.createEditor(document, project);
        EditorHighlighterFactory highlighterFactory = EditorHighlighterFactory.getInstance();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(this.document);
        EditorHighlighter highlighter;
        if (virtualFile != null) {
            highlighter = highlighterFactory.createEditorHighlighter(this.project, virtualFile);
        } else {
            highlighter = highlighterFactory.createEditorHighlighter(this.project, fileType);
        }
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

    public String getText() {
        return document.getText();
    }

    public void setText(String text) {
        executeCommand(() -> ApplicationManager.getApplication().runWriteAction(() -> this.document.setText(text)));
    }

    private void executeCommand(Runnable command) {
        CommandProcessor.getInstance().executeCommand(
                this.project,
                command,
                null,
                null,
                UndoConfirmationPolicy.DEFAULT,
                document
        );
    }

    public void resetScrollBarPosition() {
        ScrollingModelEx scrollingModel = editor.getScrollingModel();
        scrollingModel.scroll(0, 0);
    }

    public void scrollToText(List<TreeNode> path) {
        PsiElement[] wholeJson = psiFile.getChildren();
        // 一个文件中只有一个json
        JsonValue json = (JsonValue) wholeJson[0];
        if (!(json instanceof JsonContainer)) {
            return;
        }
        JsonValue jsonValue = json;
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
                            SelectionModel selectionModel = editor.getSelectionModel();
                            TextRange range = property.getTextRange();
                            selectionModel.setSelection(range.getStartOffset(), range.getEndOffset());
                            ScrollingModel scrollingModel = editor.getScrollingModel();
                            CaretModel caretModel = editor.getCaretModel();
                            caretModel.moveToOffset(range.getStartOffset());
                            scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE);
                        }
                    }
                } else if (parent instanceof ArrayNode && jsonValue instanceof JsonArray) {
                    JsonArray arr = (JsonArray) jsonValue;
                    List<JsonValue> valueList = arr.getValueList();
                    if (valueList.size() > index) {
                        jsonValue = valueList.get(index);
                        if (!it.hasNext()) {
                            SelectionModel selectionModel = editor.getSelectionModel();
                            TextRange range = jsonValue.getTextRange();
                            selectionModel.setSelection(range.getStartOffset(), range.getEndOffset());
                            ScrollingModel scrollingModel = editor.getScrollingModel();
                            CaretModel caretModel = editor.getCaretModel();
                            caretModel.moveToOffset(range.getStartOffset());
                            scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE);
                        }
                    }
                }
            }
        }
    }
}
