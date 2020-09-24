package com.jsoneditor.moddles;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.util.LocalTimeCounter;
import com.jsoneditor.Constant;

import javax.swing.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/15 21:58
 */
public class TextPanel extends NonOpaquePanel {

    private Document document;

    private Project project;

    private FileType fileType;

    private EditorEx editor;

    public TextPanel(Project project) {
        this.project = project;
        this.fileType = JsonFileType.INSTANCE;
        PsiFileFactory factory = PsiFileFactory.getInstance(project);
        PsiFile psiFile = factory.createFileFromText("JSON." + this.fileType.getDefaultExtension(),
                this.fileType, "", LocalTimeCounter.currentTime(), true, false);
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
            highlighter = highlighterFactory.createEditorHighlighter(this.project, this.fileType);
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
        CommandProcessor.getInstance().executeCommand(
                this.project,
                () -> ApplicationManager.getApplication().runWriteAction(() -> this.document.setText(text)),
                null,
                null,
                UndoConfirmationPolicy.DEFAULT,
                document
        );
    }

    public void resetScrollBarPosition() {
        JScrollPane scrollPane = editor.getScrollPane();
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setValue(horizontalScrollBar.getMinimum());
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMinimum());
    }
}
