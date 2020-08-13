package com.jsoneditor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import com.jsoneditor.action.*;
import com.jsoneditor.node.ArrayNode;
import com.jsoneditor.node.ObjectNode;
import com.jsoneditor.node.OtherNode;
import com.jsoneditor.node.StringNode;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * @Description: 开发时iml文件module节点的type要等于PLUGIN_MODULE。
 * TODO
 * 1. 按钮大小变化
 * 2. 代码整理、重构
 * @Author: zhengtao
 * @CreateDate: 2020/5/7 22:44
 */
public class JsonEditor implements ToolWindowFactory {

    private JBPanel panel = new JBPanel();
    private GridBagLayout layout = new GridBagLayout();

    // left
    private JTextArea textArea = new JTextArea();
    private JButton format = new JButton("format");
    private JButton compressJson = new JButton("compress");
    private JButton reset = new JButton("reset");

    // middle
    private JButton syncToRight = new JButton(">");
    private JButton syncToLeft = new JButton("<");

    // right
    private JButton expendJson = new JButton("expend");
    private JButton closeJson = new JButton("close");
    private JButton back = new JButton("back");
    private JButton forward = new JButton("forward");
    private Tree tree;
    private DefaultTreeModel treeModel;
    private ObjectNode root = new ObjectNode("ROOT", "");
    private TreePath movingPath;

    private JBPopupMenu contextMenus = new JBPopupMenu();
    private JBMenuItem addSub = new JBMenuItem("add child", Icons.ADD);
    private JBMenuItem addSibling = new JBMenuItem("add sibling", Icons.ADD);
    private JBMenuItem edit = new JBMenuItem("edit", Icons.EDIT);
    private JBMenuItem delete = new JBMenuItem("delete", Icons.DEL);

    private UndoManager undoManager = new UndoManager();

    public JsonEditor() {
        panel.setLayout(layout);
        paintLeft();
        paintMiddle();
        paintRight();
        addActions();

        syncToRight.doClick();

        undoManager.setLimit(10);
    }

    private void paintLeft() {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 150;
        c.weighty = 10;
        c.fill = GridBagConstraints.BOTH;
        JBPanel left = new JBPanel();
        layout.setConstraints(left, c);
        panel.add(left);
        c = new GridBagConstraints();
        c.weightx = 10;
        c.weighty = 4;
        c.ipady = -10;
        c.fill = GridBagConstraints.BOTH;
        GridBagLayout leftLayout = new GridBagLayout();
        left.setLayout(leftLayout);
        leftLayout.setConstraints(format, c);
        leftLayout.setConstraints(compressJson, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        leftLayout.setConstraints(reset, c);
        left.add(format);
        left.add(compressJson);
        left.add(reset);
        c = new GridBagConstraints();
        c.weighty = 100;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.BOTH;
        JBScrollPane scrollPane = new JBScrollPane(textArea);
        textArea.setText(temp);
        scrollPane.setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftLayout.setConstraints(scrollPane, c);
        left.add(scrollPane);
    }

    private void paintMiddle() {
        JBPanel middle = new JBPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 10;
        c.fill = GridBagConstraints.BOTH;
        layout.setConstraints(middle, c);
        panel.add(middle);
        GridBagLayout middleLayout = new GridBagLayout();
        middle.setLayout(middleLayout);
        c = new GridBagConstraints();
        c.ipadx = -35;
        c.ipady = -1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = JBUI.insets(10, 0, 10, 0);
        middleLayout.setConstraints(syncToRight, c);
        middleLayout.setConstraints(syncToLeft, c);
        middle.add(syncToRight);
        middle.add(syncToLeft);
    }

    private void paintRight() {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 100;
        c.weighty = 10;
        c.fill = GridBagConstraints.BOTH;
        JBPanel right = new JBPanel();
        layout.setConstraints(right, c);
        panel.add(right);
        c = new GridBagConstraints();
        c.weightx = 10;
        c.weighty = 2;
        c.ipady = -1;
        c.fill = GridBagConstraints.BOTH;
        GridBagLayout rightLayout = new GridBagLayout();
        right.setLayout(rightLayout);
        rightLayout.setConstraints(expendJson, c);
        rightLayout.setConstraints(closeJson, c);
        rightLayout.setConstraints(back, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        rightLayout.setConstraints(forward, c);
        right.add(expendJson);
        right.add(closeJson);
        right.add(back);
        right.add(forward);
        c = new GridBagConstraints();
        c.weighty = 100;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.BOTH;
        initTree();
        JBScrollPane scrollPane = new JBScrollPane(tree);
        scrollPane.setHorizontalScrollBarPolicy(JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        rightLayout.setConstraints(scrollPane, c);
        right.add(scrollPane);
        initContextMenu();
    }

    private void addActions() {
        format.addActionListener((e) -> {
            try {
                Object json = JSON.parse(textArea.getText(), Feature.OrderedField);
                textArea.setText(JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "json格式错误",
                        "error", JOptionPane.ERROR_MESSAGE);
            }
        });
        compressJson.addActionListener((e) -> {
            try {
                Object json = JSON.parse(textArea.getText(), Feature.OrderedField);
                textArea.setText(JSON.toJSONString(json, SerializerFeature.WriteMapNullValue));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "json格式错误",
                        "error", JOptionPane.ERROR_MESSAGE);
            }
        });
        reset.addActionListener((e) -> {
            Object json = JSON.parse(temp, Feature.OrderedField);
            textArea.setText(JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
            format.doClick();
            syncToRight.doClick();
            undoManager.discardAllEdits();
        });
        syncToRight.addActionListener((e) -> {
            try {
                root.value = JSON.parse(textArea.getText(), Feature.OrderedField);
                Utils.refreshTree(root);
                tree.expandPath(new TreePath(root.getPath()));
                tree.updateUI();
                undoManager.discardAllEdits();
            } catch (JSONException ex) {
                JOptionPane.showMessageDialog(panel, "json格式错误",
                        "error", JOptionPane.ERROR_MESSAGE);
            }
        });
        syncToLeft.addActionListener((e) -> {
            Utils.refreshJson(root);
            textArea.setText(JSON.toJSONString(root.value, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
        });
        expendJson.addActionListener((e) -> expandTree(tree, new TreePath(root)));
        closeJson.addActionListener((e) -> collapseTree(tree, new TreePath(root)));
        back.addActionListener((e) -> {
            try {
                undoManager.undo();
            } catch (CannotUndoException ex) {
            }
        });
        forward.addActionListener((e) -> {
            try {
                undoManager.redo();
            } catch (CannotRedoException ex) {
            }
        });
    }

    private void initTree() {
        tree = new Tree(root);
        treeModel = (DefaultTreeModel) tree.getModel();
        tree.setCellRenderer(new CustomTreeCellRenderer());
        tree.setRowHeight(30);
        tree.setDragEnabled(true);
        tree.setTransferHandler(new TransferHandler("drag node."));
        tree.expandPath(new TreePath(root.getPath()));
        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isMetaDown()) {
                    TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
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
                                undoManager.addEdit(dragAction);
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
                new AddOrEdit(select, 1, (node, selectNode) -> {
                    TreeAction action;
                    if (selectNode instanceof StringNode || selectNode instanceof OtherNode) {
                        TreeNode newSelect = TreeNode.getNode(selectNode.key, new JSONObject(true));
                        newSelect.add(node);
                        action = new ReplaceAction(tree, newSelect, selectNode, false);
                    } else {
                        action = new AddAction(tree, node, selectNode, selectNode.getChildCount());
                    }
                    action.doAction();
                    undoManager.addEdit(action);
                });
            }
        });
        addSibling.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                TreeNode parent = select.getParent();
                new AddOrEdit(select, 2, (node, selectNode) -> {
                    int index = parent.getIndex(selectNode) + 1;
                    AddAction action = new AddAction(tree, node, parent, index);
                    action.doAction();
                    undoManager.addEdit(action);
                });
            }
        });
        edit.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                new AddOrEdit(select, 3, (node, selectNode) -> {
                    boolean keepChildren = false;
                    if (node instanceof ObjectNode || node instanceof ArrayNode) {
                        if (selectNode instanceof ObjectNode || selectNode instanceof ArrayNode) {
                            node.attachChildrenFromAnotherNode(selectNode);
                            keepChildren = true;
                        }
                    }
                    ReplaceAction action = new ReplaceAction(tree, node, selectNode, keepChildren);
                    action.doAction();
                    undoManager.addEdit(action);
                });
            }
        });
        delete.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode[] selectedNodes = tree.getSelectedNodes(TreeNode.class, null);
                DeleteAction action = new DeleteAction(tree, selectedNodes);
                action.doAction();
                undoManager.addEdit(action);
            }
        });
    }

    private void expandTree(Tree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandTree(tree, path);
            }
        }
        tree.expandPath(parent);
    }

    private void collapseTree(Tree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                collapseTree(tree, path);
            }
        }
        tree.collapsePath(parent);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    class AddOrEdit extends JDialog {

        private JBLabel typeLabel = new JBLabel("type");

        private ComboBox<SelectItem> type = new ComboBox<SelectItem>() {{
            addItem(SelectItem.String);
            addItem(SelectItem.Object);
            addItem(SelectItem.Array);
            addItem(SelectItem.Other);
        }};

        private JBTextField key = new JBTextField();

        private JBLabel keyLabel = new JBLabel("key");

        private JBTextField value = new JBTextField();

        private JBLabel valueLabel = new JBLabel("value");

        private JButton ok = new JButton("ok");

        private JButton cancel = new JButton("cancel");

        private TreeNode selectNode;

        private String title = "Add";

        private BiConsumer<TreeNode, TreeNode> callback;

        public AddOrEdit(TreeNode node, Integer opt, BiConsumer<TreeNode, TreeNode> callback) {
            this.selectNode = node;
            this.callback = callback;
            // 1、2、3分别代表新增子节点、新增兄弟节点、编辑节点
            if (opt == 1) {
                type.setSelectedItem(SelectItem.String);
                if (node instanceof ArrayNode) {
                    key.setVisible(false);
                    keyLabel.setVisible(false);
                }
            } else if (opt == 2) {
                type.setSelectedItem(SelectItem.String);
                TreeNode parent = node.getParent();
                if (parent instanceof ArrayNode) {
                    key.setVisible(false);
                    keyLabel.setVisible(false);
                }
            } else if (opt == 3) {
                title = "Edit";
                if (node instanceof ObjectNode) {
                    type.setSelectedItem(SelectItem.Object);
                    value.setVisible(false);
                    valueLabel.setVisible(false);
                } else if (node instanceof ArrayNode) {
                    type.setSelectedItem(SelectItem.Array);
                    value.setVisible(false);
                    valueLabel.setVisible(false);
                } else if (node instanceof StringNode) {
                    type.setSelectedItem(SelectItem.String);
                } else {
                    type.setSelectedItem(SelectItem.Other);
                }
                TreeNode parent = selectNode.getParent();
                if (parent instanceof ArrayNode) {
                    key.setVisible(false);
                    keyLabel.setVisible(false);
                }
            }
            key.setText(opt != 3 ? "key" : node.key);
            value.setText(opt != 3 ? "value" : (node.value == null ? "null" : node.value.toString()));
            openDialog();
        }

        private void openDialog() {
            setTitle(title);
            setSize(300, 180);
            setLocationRelativeTo(panel);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setResizable(false);
            setModal(true);
            JBPanel p = new JBPanel();
            add(p);
            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 10;
            c.anchor = GridBagConstraints.CENTER;
            layout.setConstraints(typeLabel, c);
            c.weightx = 20;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.BOTH;
            c.gridwidth = 2;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = JBUI.insets(0, 0, 5, 15);
            layout.setConstraints(type, c);
            c = new GridBagConstraints();
            c.weightx = 10;
            c.anchor = GridBagConstraints.CENTER;
            layout.setConstraints(keyLabel, c);
            c.weightx = 20;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.BOTH;
            c.gridwidth = 2;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = JBUI.insets(0, 0, 5, 15);
            layout.setConstraints(key, c);
            c = new GridBagConstraints();
            c.weightx = 10;
            c.anchor = GridBagConstraints.CENTER;
            layout.setConstraints(valueLabel, c);
            c.weightx = 20;
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.BOTH;
            c.gridwidth = 2;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = JBUI.insets(0, 0, 5, 15);
            layout.setConstraints(value, c);
            c = new GridBagConstraints();
            c.weightx = 30;
            c.gridx = 1;
            c.gridy = 3;
            c.anchor = GridBagConstraints.EAST;
            c.insets = JBUI.insets(10, 0, 5, 0);
            layout.setConstraints(ok, c);
            c.gridx = 2;
            c.weightx = 5;
            c.anchor = GridBagConstraints.WEST;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.insets = JBUI.insets(10, 5, 5, 15);
            layout.setConstraints(cancel, c);
            p.setLayout(layout);
            p.add(typeLabel);
            p.add(type);
            p.add(keyLabel);
            p.add(key);
            p.add(valueLabel);
            p.add(value);
            p.add(ok);
            p.add(cancel);
            addAction();
            setVisible(true);
        }

        private void addAction() {
            type.addItemListener((e) -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    SelectItem select = (SelectItem) type.getSelectedItem();
                    if (SelectItem.Object.equals(select) || SelectItem.Array.equals(select)) {
                        value.setVisible(false);
                        valueLabel.setVisible(false);
                    } else {
                        value.setVisible(true);
                        valueLabel.setVisible(true);
                    }
                }
            });
            ok.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    String nodeKey = key.getText();
                    Object nodeValue;
                    SelectItem nodeType = (SelectItem) type.getSelectedItem();
                    if (SelectItem.Object.equals(nodeType)) {
                        nodeValue = new JSONObject(true);
                    } else if (SelectItem.Array.equals(nodeType)) {
                        nodeValue = new JSONArray();
                    } else if (SelectItem.String.equals(nodeType)) {
                        nodeValue = value.getText();
                    } else {
                        String valueStr = value.getText();
                        if ("".equals(valueStr)) {
                            nodeValue = null;
                        } else if ("true".equalsIgnoreCase(valueStr) || "false".equalsIgnoreCase(valueStr)) {
                            nodeValue = Boolean.parseBoolean(valueStr);
                        } else if (Utils.isInteger(valueStr)) {
                            nodeValue = Long.parseLong(valueStr);
                        } else if (Utils.isFloat(valueStr)) {
                            nodeValue = Double.parseDouble(valueStr);
                        } else {
                            nodeValue = value.getText();
                        }
                    }
                    TreeNode returnNode = TreeNode.getNode(nodeKey, nodeValue);
                    callback.accept(returnNode, selectNode);
                    AddOrEdit.this.dispose();
                }
            });
            cancel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    AddOrEdit.this.dispose();
                }
            });
        }
    }

    enum SelectItem {

        // types
        Object,
        Array,
        String,
        Other;

    }

    public static void main(String[] args) {
        JsonEditor jsonEditor = new JsonEditor();
        JFrame jFrame = new JFrame("com.jsoneditor.JsonEditor");
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(800, 500);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.add(jsonEditor.panel);
    }

    private static String temp = "{\n" +
            "\t\"name\":\"zhengt\",\n" +
            "\t\"age\":18,\n" +
            "\t\"isHandsome\":true,\n" +
            "\t\"email\":\"hj_zhengt@163.com\",\n" +
            "\t\"address\":{\n" +
            "\t\t\"country\":\"China\",\n" +
            "\t\t\"province\":\"jiangsu\",\n" +
            "\t\t\"city\":\"nanjing\"\n" +
            "\t},\n" +
            "\t\"hobbys\":[\n" +
            "\t\t{\n" +
            "\t\t\t\"order\":\"first\",\n" +
            "\t\t\t\"name\":\"coding\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"order\":\"second\",\n" +
            "\t\t\t\"name\":\"fishing\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"order\":\"third\",\n" +
            "\t\t\t\"name\":\"whatever\"\n" +
            "\t\t}\n" +
            "\t]\n" +
            "}";

}
