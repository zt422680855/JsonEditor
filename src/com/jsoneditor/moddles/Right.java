package com.jsoneditor.moddles;

import com.alibaba.fastjson.JSONObject;
import com.intellij.json.psi.JsonContainer;
import com.intellij.json.psi.JsonElement;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonProperty;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.psi.PsiElement;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeModelAdapter;
import com.jsoneditor.CustomTreeCellRenderer;
import com.jsoneditor.TreeUtils;
import com.jsoneditor.Undo;
import com.jsoneditor.edits.*;
import com.jsoneditor.node.ContainerNode;
import com.jsoneditor.node.LeafNode;
import com.jsoneditor.node.ObjectNode;
import com.jsoneditor.node.TreeNode;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 21:51
 */
public class Right extends JsonEditorModdle {

    private static final String DEFAULT_SEATCH_TEXT = "search...";

    private JBTextField search = new JBTextField(DEFAULT_SEATCH_TEXT) {{
        setForeground(JBColor.GRAY);
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                search.setForeground(JBColor.BLACK);
                if (DEFAULT_SEATCH_TEXT.equals(search.getText())) {
                    search.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if ("".equals(search.getText())) {
                    search.setForeground(JBColor.GRAY);
                    search.setText(DEFAULT_SEATCH_TEXT);
                }
            }
        });

        getDocument().addDocumentListener(new DocumentAdapter() {

            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent) {
                TreeNode root = getRoot();
                String text = search.getText();
                if (!DEFAULT_SEATCH_TEXT.equals(text) && text.length() >= 3) {
                    root.recursiveOperation((node) -> {
                        String nodeText = node.getText();
                        javax.swing.tree.TreeNode[] nodes = ((DefaultTreeModel) tree.getModel()).getPathToRoot(node);
                        TreePath path = new TreePath(nodes);
                        if (nodeText.toLowerCase().lastIndexOf(text.toLowerCase()) > -1) {
                            TreePath[] selectionPaths = tree.getSelectionPaths();
                            if (selectionPaths == null || selectionPaths.length == 0) {
                                tree.setSelectionPath(path);
                                tree.scrollPathToVisible(path);
                            }
                            node.expandByNode(tree);
                            node.filter = true;
                        } else {
                            node.filter = false;
                        }
                    });
                } else {
                    root.recursiveOperation((node) -> node.filter = false);
                }
                tree.updateUI();
            }

        });
    }};

    public Tree tree;
    private TreePath movingPath;
    private JBLabel movingLabel = new JBLabel();
    private final JWindow window = new JWindow();

    private JBPopupMenu contextMenus = new JBPopupMenu();
    private JBMenuItem addSub = new JBMenuItem("add child", Icons.ADD);
    private JBMenuItem addSibling = new JBMenuItem("add sibling", Icons.ADD);
    private JBMenuItem edit = new JBMenuItem("edit", Icons.EDIT);
    private JBMenuItem delete = new JBMenuItem("delete", Icons.DEL);
    private JBMenuItem copy = new JBMenuItem("copy", Icons.COPY);
    private JBMenuItem copyKey = new JBMenuItem("copy key", Icons.COPY_KEY);
    private JBMenuItem copyValue = new JBMenuItem("copy value", Icons.COPY_VALUE);

    public Right(Project project, JsonEditorModdle parent) {
        super(project, parent);
        paint();
        initContextMenu();
    }

    private void paint() {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 100;
        c.weighty = 2;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(search, c);
        add(search);
        c = new GridBagConstraints();
        c.weighty = 200;
        c.fill = GridBagConstraints.BOTH;
        initTree();
        JBScrollPane scrollPane = new JBScrollPane(tree);
        scrollPane.setHorizontalScrollBarPolicy(JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        layout.setConstraints(scrollPane, c);
        add(scrollPane);
    }

    private void initTree() {
        TreeNode root = new ObjectNode("ROOT", new JSONObject(true));
        tree = new Tree(root);
        tree.getModel().addTreeModelListener(new TreeModelAdapter() {

            @Override
            public void treeNodesInserted(TreeModelEvent event) {
                search.setText(search.getText());
            }

            @Override
            public void treeNodesRemoved(TreeModelEvent event) {
                search.setText(search.getText());
            }
        });
        tree.setCellRenderer(new CustomTreeCellRenderer());
        tree.setRowHeight(30);
        tree.setDragEnabled(true);
        tree.setTransferHandler(new TransferHandler("drag node."));
        tree.expandPath(new TreePath(root.getPath()));
        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                if (select != null) {
                    if (e.isMetaDown()) {
                        if (select.isRoot()) {
                            addSibling.setVisible(false);
                            copy.setVisible(false);
                            copyKey.setVisible(false);
                            copyValue.setVisible(false);
                            edit.setVisible(false);
                            delete.setVisible(false);
                        } else {
                            addSibling.setVisible(true);
                            copy.setVisible(true);
                            copyKey.setVisible(true);
                            copyValue.setVisible(true);
                            edit.setVisible(true);
                            delete.setVisible(true);
                        }
                        contextMenus.show(tree, e.getX(), e.getY());
                    } else {
                        int clickCount = e.getClickCount();
                        if (clickCount == 2) {
                            // 双击叶子节点
                            if (select.isLeaf()) {
                                new AddOrEdit(project, select, 3, (node, selectNode) -> {
                                    ReplaceEdit action = new ReplaceEdit(tree, node, selectNode, false);
                                    action.doAction();
                                    Undo.addAction(project, action);
                                });
                            }
                        } else {
                            ModdleContext.scrollToText(project, select.getFullPath());
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (movingPath != null) {
                    TreePath pathWhenReleased = tree.getPathForLocation(e.getX(), e.getY());
                    if (pathWhenReleased != null) {
                        if (!movingPath.isDescendant(pathWhenReleased)) {
                            Optional.ofNullable(pathWhenReleased.getLastPathComponent()).ifPresent((curNode) -> {
                                TreeNode target = (TreeNode) curNode;
                                TreeNode movingNode = (TreeNode) movingPath.getLastPathComponent();
                                TreeNode cloneNode = movingNode.clone();
                                AddEdit addAction;
                                if (target instanceof ContainerNode) {
                                    addAction = new AddEdit(tree, cloneNode, target, target.getChildCount());
                                } else {
                                    TreeNode parent = target.getParent();
                                    int newIndex = parent.getIndex(target);
                                    if (!parent.equals(movingNode.getParent()) || newIndex > parent.getIndex(movingNode)) {
                                        newIndex += 1;
                                    }
                                    addAction = new AddEdit(tree, cloneNode, parent, newIndex);
                                }
                                DeleteEdit delAction = new DeleteEdit(tree, new TreeNode[]{movingNode});
                                // 一个拖拽动作等效为一个插入和删除节点动作
                                DragEdit dragAction = new DragEdit(tree, addAction, delAction);
                                dragAction.doAction();
                                Undo.addAction(project, dragAction);
                            });
                        }
                    }
                    movingPath = null;
                    window.setVisible(false);
                }
            }
        });
        tree.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (movingPath == null) {
                    TreePath pathWhenPressed = tree.getPathForLocation(e.getX(), e.getY());
                    if (pathWhenPressed != null) {
                        movingPath = pathWhenPressed;
                    }
                } else {
                    TreeNode node = (TreeNode) movingPath.getLastPathComponent();
                    movingLabel.setText(node.getText());
                    movingLabel.setIcon(node.icon());
                    if (movingLabel.getParent() == null) {
                        window.add(movingLabel);
                        window.pack();
                    }
                    Point pt = new Point(0, e.getY() - movingLabel.getHeight() / 2);
                    Component c = (Component) e.getSource();
                    SwingUtilities.convertPointToScreen(pt, c);
                    window.setLocation(pt);
                    window.setVisible(true);
                    window.setSize(getWidth() - 2, 30);
                    TreePath targetPath = tree.getPathForLocation(e.getX(), e.getY());
                    if (targetPath != null) {
                        TreeNode targetNode = (TreeNode) targetPath.getLastPathComponent();
                        if (targetNode instanceof ContainerNode) {
                            tree.expandPath(targetPath);
                        }
                    }
                }
            }

        });
    }

    private void initContextMenu() {
        contextMenus.add(addSub);
        contextMenus.add(addSibling);
        contextMenus.add(edit);
        contextMenus.add(delete);
        contextMenus.addSeparator();
        contextMenus.add(copy);
        contextMenus.add(copyKey);
        contextMenus.add(copyValue);
        addSub.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                new AddOrEdit(project, select, 1, (node, selectNode) -> {
                    TreeEdit edit;
                    if (selectNode instanceof LeafNode) {
                        TreeNode newSelect = TreeUtils.getNode(selectNode.key, new JSONObject(true));
                        newSelect.add(node);
                        edit = new ReplaceEdit(tree, newSelect, selectNode, false);
                    } else {
                        edit = new AddEdit(tree, node, selectNode, selectNode.getChildCount());
                    }
                    edit.doAction();
                    Undo.addAction(project, edit);
                });
            }
        });
        addSibling.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                TreeNode parent = select.getParent();
                new AddOrEdit(project, select, 2, (node, selectNode) -> {
                    int index = parent.getIndex(selectNode) + 1;
                    AddEdit edit = new AddEdit(tree, node, parent, index);
                    edit.doAction();
                    Undo.addAction(project, edit);
                });
            }
        });
        edit.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                new AddOrEdit(project, select, 3, (node, selectNode) -> {
                    boolean keepChildren = false;
                    if (node instanceof ContainerNode) {
                        if (selectNode instanceof ContainerNode) {
                            node.attachChildrenFromAnotherNode(selectNode);
                            keepChildren = true;
                        }
                    }
                    ReplaceEdit edit = new ReplaceEdit(tree, node, selectNode, keepChildren);
                    edit.doAction();
                    Undo.addAction(project, edit);
                });
            }
        });
        delete.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode[] selectedNodes = tree.getSelectedNodes(TreeNode.class, null);
                DeleteEdit edit = new DeleteEdit(tree, selectedNodes);
                edit.doAction();
                Undo.addAction(project, edit);
            }
        });
        copy.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                TreeNode copyNode = select.clone();
                TreeNode parent = select.getParent();
                int index = parent.getIndex(select) + 1;
                AddEdit edit = new AddEdit(tree, copyNode, parent, index);
                edit.doAction();
                tree.setSelectionPath(new TreePath(copyNode.getPath()));
                Undo.addAction(project, edit);
            }
        });
        copyKey.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(select.key), null);
            }
        });
        copyValue.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(select.valueString()), null);
            }
        });
    }

    public TreeNode getRoot() {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        return (TreeNode) model.getRoot();
    }

    public void setRoot(TreeNode root) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(root);
    }

    public void scrollToTreeNode(java.util.List<JsonElement> elements) {
        TreeNode mapperNode = (TreeNode) tree.getModel().getRoot();
        PsiElement parent;
        for (JsonElement jsonEle : elements) {
            parent = jsonEle.getParent();
            if (parent instanceof JsonFile || jsonEle instanceof JsonProperty) {
                continue;
            }
            if (parent instanceof JsonProperty) {
                JsonContainer grandpa = (JsonContainer) parent.getParent();
                PsiElement[] children = grandpa.getChildren();
                for (int i = 0; i < children.length; i++) {
                    PsiElement child = children[i];
                    if (parent.equals(child) && !mapperNode.isLeaf() && i < mapperNode.getChildCount()) {
                        mapperNode = (TreeNode) mapperNode.getChildAt(i);
                        break;
                    }
                }
            } else {
                PsiElement[] children = parent.getChildren();
                for (int i = 0; i < children.length; i++) {
                    PsiElement child = children[i];
                    if (jsonEle.equals(child) && !mapperNode.isLeaf() && i < mapperNode.getChildCount()) {
                        mapperNode = (TreeNode) mapperNode.getChildAt(i);
                        break;
                    }
                }
            }
        }
        TreePath scrollPath = new TreePath(mapperNode.getPath());
        tree.setSelectionPath(scrollPath);
        tree.scrollPathToVisible(scrollPath);
        JBScrollPane scrollPane = (JBScrollPane) (tree.getParent().getParent());
        scrollPane.getHorizontalScrollBar().setValue(0);
    }

}
