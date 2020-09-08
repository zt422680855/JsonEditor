package com.jsoneditor;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.util.LocalTimeCounter;

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
        PsiFile psiFile = factory.createFileFromText("Text." + this.fileType.getDefaultExtension(),
                this.fileType, "", LocalTimeCounter.currentTime(), true, false);
        DaemonCodeAnalyzer.getInstance(project).setHighlightingEnabled(psiFile, false);
        this.document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        EditorFactory editorFactory = EditorFactory.getInstance();
        this.editor = (EditorEx) editorFactory.createEditor(document, project);
        setupTextFieldEditor(editor);
        editor.setCaretEnabled(true);
        this.add(editor.getComponent());
    }

    public static void setupTextFieldEditor(EditorEx editor) {
        EditorSettings settings = editor.getSettings();
        settings.setAdditionalLinesCount(0);
        settings.setAdditionalColumnsCount(1);
        settings.setRightMarginShown(false);
        settings.setRightMargin(-1);
        settings.setFoldingOutlineShown(true);
        settings.setLineNumbersShown(true);
        settings.setLineMarkerAreaShown(true);
        settings.setIndentGuidesShown(true);
        settings.setVirtualSpace(false);
        settings.setWheelFontChangeEnabled(false);
        settings.setAdditionalPageAtBottom(false);
        editor.setHorizontalScrollbarVisible(true);
        editor.setVerticalScrollbarVisible(true);
        settings.setLineCursorWidth(1);
    }
}
