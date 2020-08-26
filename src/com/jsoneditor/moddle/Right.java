package com.jsoneditor.moddle;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import com.jsoneditor.AddOrEdit;
import com.jsoneditor.CustomTreeCellRenderer;
import com.jsoneditor.TreeNode;
import com.jsoneditor.Undo;
import com.jsoneditor.action.*;
import com.jsoneditor.node.ArrayNode;
import com.jsoneditor.node.ObjectNode;
import com.jsoneditor.node.OtherNode;
import com.jsoneditor.node.StringNode;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
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
public class Right extends JBPanel {

    private JBPanel parentPanel;

    private GridBagLayout layout;

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
                    root.recursionOption((node) -> {
                        String userObject = node.getUserObject();
                        javax.swing.tree.TreeNode[] nodes = ((DefaultTreeModel) tree.getModel()).getPathToRoot(node);
                        TreePath path = new TreePath(nodes);
                        if (userObject.toLowerCase().lastIndexOf(text.toLowerCase()) > -1) {
                            TreePath[] selectionPaths = tree.getSelectionPaths();
                            if (selectionPaths == null || selectionPaths.length == 0) {
                                tree.setSelectionPath(path);
                                tree.scrollPathToVisible(path);
                            }
                            node.enpandByNode(tree);
                            node.filter = true;
                        } else {
                            node.filter = false;
                        }
                    });
                } else {
                    root.recursionOption((node) -> node.filter = false);
                }
                tree.updateUI();
            }

        });
    }};

    public Tree tree;
    private TreePath movingPath;

    private JBPopupMenu contextMenus = new JBPopupMenu();
    private JBMenuItem addSub = new JBMenuItem("add child", Icons.ADD);
    private JBMenuItem addSibling = new JBMenuItem("add sibling", Icons.ADD);
    private JBMenuItem edit = new JBMenuItem("edit", Icons.EDIT);
    private JBMenuItem delete = new JBMenuItem("delete", Icons.DEL);

    public Right(JBPanel panel) {
        this.parentPanel = panel;
        this.layout = new GridBagLayout();
        setLayout(this.layout);
        paintRight();
    }

    private void paintRight() {
        GridBagLayout parentLayout = (GridBagLayout) parentPanel.getLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 100;
        c.weighty = 200;
        c.fill = GridBagConstraints.BOTH;
        parentLayout.setConstraints(this, c);
        parentPanel.add(this);
        c = new GridBagConstraints();
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
        initContextMenu();
    }

    private void initTree() {
        TreeNode root = new ObjectNode("ROOT", new JSONObject(true));
        tree = new Tree(root);
        tree.setCellRenderer(new CustomTreeCellRenderer());
        tree.setRowHeight(30);
        tree.setDragEnabled(true);
        tree.setTransferHandler(new TransferHandler("drag node."));
        tree.expandPath(new TreePath(root.getPath()));
        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                if (e.isMetaDown()) {
                    if (select.isRoot()) {
                        addSibling.setVisible(false);
                        edit.setVisible(false);
                        delete.setVisible(false);
                    } else {
                        addSibling.setVisible(true);
                        edit.setVisible(true);
                        delete.setVisible(true);
                    }
                    contextMenus.show(tree, e.getX(), e.getY());
                } else {
                    int clickCount = e.getClickCount();
                    if (clickCount == 2) {
                        // 双击叶子节点
                        if (select.isLeaf()) {
                            new AddOrEdit(parentPanel, select, 3, (node, selectNode) -> {
                                ReplaceAction action = new ReplaceAction(tree, node, selectNode, false);
                                action.doAction();
                                Undo.addAction(action);
                            });
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
                                AddAction addAction;
                                if (target instanceof ObjectNode || target instanceof ArrayNode) {
                                    addAction = new AddAction(tree, cloneNode, target, target.getChildCount());
                                } else {
                                    TreeNode parent = target.getParent();
                                    int newIndex = parent.getIndex(target);
                                    if (!parent.equals(movingNode.getParent()) || newIndex > parent.getIndex(movingNode)) {
                                        newIndex += 1;
                                    }
                                    addAction = new AddAction(tree, cloneNode, parent, newIndex);
                                }
                                DeleteAction delAction = new DeleteAction(tree, new TreeNode[]{movingNode});
                                // 一个拖拽动作等效为一个插入和删除节点动作
                                DragAction dragAction = new DragAction(tree, addAction, delAction);
                                dragAction.doAction();
                                Undo.addAction(dragAction);
                            });
                        }
                    }
                    movingPath = null;
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
                }
            }
        });
    }

    private void initContextMenu() {
        contextMenus.add(addSub);
        contextMenus.add(addSibling);
        contextMenus.add(edit);
        contextMenus.add(delete);
        addSub.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                new AddOrEdit(parentPanel, select, 1, (node, selectNode) -> {
                    TreeAction action;
                    if (selectNode instanceof StringNode || selectNode instanceof OtherNode) {
                        TreeNode newSelect = TreeNode.getNode(selectNode.key, new JSONObject(true));
                        newSelect.add(node);
                        action = new ReplaceAction(tree, newSelect, selectNode, false);
                    } else {
                        action = new AddAction(tree, node, selectNode, selectNode.getChildCount());
                    }
                    action.doAction();
                    Undo.addAction(action);
                });
            }
        });
        addSibling.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                TreeNode parent = select.getParent();
                new AddOrEdit(parentPanel, select, 2, (node, selectNode) -> {
                    int index = parent.getIndex(selectNode) + 1;
                    AddAction action = new AddAction(tree, node, parent, index);
                    action.doAction();
                    Undo.addAction(action);
                });
            }
        });
        edit.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                new AddOrEdit(parentPanel, select, 3, (node, selectNode) -> {
                    boolean keepChildren = false;
                    if (node instanceof ObjectNode || node instanceof ArrayNode) {
                        if (selectNode instanceof ObjectNode || selectNode instanceof ArrayNode) {
                            node.attachChildrenFromAnotherNode(selectNode);
                            keepChildren = true;
                        }
                    }
                    ReplaceAction action = new ReplaceAction(tree, node, selectNode, keepChildren);
                    action.doAction();
                    Undo.addAction(action);
                });
            }
        });
        delete.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode[] selectedNodes = tree.getSelectedNodes(TreeNode.class, null);
                DeleteAction action = new DeleteAction(tree, selectedNodes);
                action.doAction();
                Undo.addAction(action);
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

}
