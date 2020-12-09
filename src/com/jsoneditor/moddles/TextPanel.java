package com.jsoneditor.moddles;

import com.intellij.codeInsight.actions.FileInEditorProcessor;
import com.intellij.codeInsight.actions.LastRunReformatCodeOptionsProvider;
import com.intellij.codeInsight.actions.ReformatCodeRunOptions;
import com.intellij.codeInsight.actions.TextRangeType;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.json.JsonFileType;
import com.intellij.json.psi.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.ScrollingModelEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
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
import com.jsoneditor.persist.JsonEditorPersistentState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @Description: 文档实例被对应的虚拟文件实例微弱的引用。因此，如果不再被引用，一篇未修改的文档实例可以被垃圾回收，
 * 而如果修改文档内容之后又再次被访问，新的实例将被创建。将文档引用保存在你的插件的持久数据结构中将导致内存泄漏。
 * https://blog.csdn.net/hawkdowen/article/details/41047229
 * @Author: zhengt
 * @CreateDate: 2020/8/15 21:58
 */
public class TextPanel extends NonOpaquePanel {

    private Project project;

    private PsiFile psiFile;

    private EditorEx editor;

    private FileInEditorProcessor formatProcessor;

    private JsonEditorPersistentState state;

    public TextPanel(Project project) {
        this.project = project;
        JsonFileType fileType = JsonFileType.INSTANCE;
        PsiFileFactory factory = PsiFileFactory.getInstance(project);
        this.psiFile = factory.createFileFromText("JSON." + fileType.getDefaultExtension(),
                fileType, "", LocalTimeCounter.currentTime(), true, false);
        DaemonCodeAnalyzer.getInstance(project).setHighlightingEnabled(psiFile, true);
        // document只能在方法栈中使用，随着方法结束，对象可以被回收。不能保存在成员变量、静态变量，否则内存泄露
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

        state = ServiceManager.getService(project, JsonEditorPersistentState.class);
        setText(state.getText(project.getName()));
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                state.setText(project.getName(), document.getText());
            }
        });

        // 光标事件
        caretListener();

        // idea默认代码格式化
        LastRunReformatCodeOptionsProvider provider = new LastRunReformatCodeOptionsProvider(PropertiesComponent.getInstance());
        ReformatCodeRunOptions lastRunOptions = provider.getLastRunOptions(psiFile);
        lastRunOptions.setProcessingScope(TextRangeType.WHOLE_FILE);
        formatProcessor = new FileInEditorProcessor(psiFile, editor, lastRunOptions);
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

    private void caretListener() {
        CaretModel caretModel = editor.getCaretModel();
        caretModel.addCaretListener(new CaretListener() {

            @Override
            public void caretPositionChanged(@NotNull CaretEvent event) {
                Caret caret = caretModel.getCurrentCaret();
                PsiElement ele = psiFile.findElementAt(caret.getOffset());
                if (ele != null) {
                    // 当前光标所在位置元素
                    PsiElement element = ele.getParent();
                    if (element instanceof JsonElement) {
                        JsonElement jsonElement = (JsonElement) element;
                        LinkedList<JsonElement> jsonElements = new LinkedList<>();
                        for (; ; ) {
                            jsonElements.push(jsonElement);
                            PsiElement parent = jsonElement.getParent();
                            if (parent instanceof JsonFile) {
                                break;
                            } else {
                                jsonElement = (JsonElement) parent;
                            }
                        }
                        ModdleContext.scrollToTreeNode(project, jsonElements);
                    }
                }
            }

        });
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

    public void format() {
        formatProcessor.processCode();
    }

    public void resetScrollBarPosition() {
        ScrollingModelEx scrollingModel = editor.getScrollingModel();
        scrollingModel.scroll(0, 0);
    }

    public void scrollToText(List<TreeNode> path) {
        PsiElement[] content = psiFile.getChildren();
        Arrays.stream(content).filter(ele ->
                !StringUtil.isEmpty(ele.getText()) && ele instanceof JsonContainer
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
