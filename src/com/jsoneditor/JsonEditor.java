package com.jsoneditor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.intellij.util.ui.tree.TreeModelAdapter;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @Description:
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
    private TreeNode root = new TreeNode("ROOT", "");
    private TreePath movingPath;

    private JBPopupMenu contextMenus = new JBPopupMenu();
    private JBMenuItem addSub = new JBMenuItem("add child", Icons.ADD);
    private JBMenuItem addSibling = new JBMenuItem("add sibling", Icons.ADD);
    private JBMenuItem edit = new JBMenuItem("edit", Icons.EDIT);
    private JBMenuItem delete = new JBMenuItem("delete", Icons.DEL);

    public JsonEditor() {
        panel.setLayout(layout);
        paintLeft();
        paintMiddle();
        paintRight();
        addActions();
        initContextMenu();

        textArea.setText(temp);
        syncToRight.doClick();
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
                new AddOrEdit(select, true, (node) -> {
                    node.updateNode();
                    treeModel.insertNodeInto(node, select, select.getChildCount());
                    select.updateNode();
                    tree.expandPath(new TreePath(select.getPath()));
                });
            }
        });
        addSibling.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                TreeNode parent = (TreeNode) select.getParent();
                new AddOrEdit(select, true, (node) -> {
                    node.updateNode();
                    int index = parent.getIndex(select) + 1;
                    treeModel.insertNodeInto(node, parent, index);
                    parent.updateNode();
                });
            }
        });
        edit.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                new AddOrEdit(select, false, (node) -> {
                    node.updateNode();
                });
            }
        });
        delete.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode[] selectedNodes = tree.getSelectedNodes(TreeNode.class, null);
                for (TreeNode node : selectedNodes) {
                    TreeNode parent = (TreeNode) node.getParent();
                    treeModel.removeNodeFromParent(node);
                    parent.updateNode();
                }
            }
        });
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
    }

    private void initTree() {
        tree = new Tree(root);
        treeModel = (DefaultTreeModel) tree.getModel();
        treeModel.addTreeModelListener(new TreeModelAdapter() {
            @Override
            public void treeStructureChanged(TreeModelEvent event) {
                System.out.println("structure change");
            }

            @Override
            public void treeNodesChanged(TreeModelEvent event) {
                System.out.println("change");
            }

            @Override
            public void treeNodesInserted(TreeModelEvent event) {
                System.out.println("add");
            }

            @Override
            public void treeNodesRemoved(TreeModelEvent event) {
                System.out.println("del");
            }
        });
        tree.setRowHeight(30);
        tree.setDragEnabled(true);
        tree.setTransferHandler(new TransferHandler("drag node."));
        tree.expandPath(new TreePath(root.getPath()));
        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isMetaDown()) {
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
                                TreeNode movingNodeParent = (TreeNode) movingNode.getParent();
                                TreeNode cloneNode = movingNode.clone();
                                if (TreeNode.OBJECT.equals(target.type)) {
                                    treeModel.insertNodeInto(cloneNode, target, target.getChildCount());
                                    target.updateNode();
                                } else {
                                    Optional.ofNullable(target.getParent()).ifPresent((parentNode) -> {
                                        TreeNode parent = (TreeNode) parentNode;
                                        int newIndex = parent.getIndex(target);
                                        if (!parent.equals(movingNode.getParent()) || newIndex > parent.getIndex(movingNode)) {
                                            newIndex += 1;
                                        }
                                        treeModel.insertNodeInto(cloneNode, parent, newIndex);
                                        parent.updateNode();
                                    });
                                }
                                treeModel.removeNodeFromParent(movingNode);
                                movingNodeParent.updateNode();
                                tree.setSelectionPath(new TreePath(cloneNode.getPath()));
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

    private void addActions() {
        format.addActionListener((e) -> {
            Object json = JSON.parse(textArea.getText(), Feature.OrderedField);
            textArea.setText(JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
        });
        compressJson.addActionListener((e) -> {
            Object json = JSON.parse(textArea.getText(), Feature.OrderedField);
            textArea.setText(JSON.toJSONString(json, SerializerFeature.WriteMapNullValue));
        });
        reset.addActionListener((e) -> {
            Object json = JSON.parse(temp, Feature.OrderedField);
            textArea.setText(JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
            format.doClick();
            syncToRight.doClick();
        });
        syncToRight.addActionListener((e) -> {
            root.removeAllChildren();
            root.value = JSON.parse(textArea.getText(), Feature.OrderedField);
            root.loadTreeNodes();
            tree.expandPath(new TreePath(root.getPath()));
            tree.updateUI();
        });
        syncToLeft.addActionListener((e) -> {
            refreshTreeData(root);
            textArea.setText(JSON.toJSONString(root.value, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
        });
        expendJson.addActionListener((e) -> expandTree(tree, new TreePath(root)));
        closeJson.addActionListener((e) -> collapseTree(tree, new TreePath(root)));
        back.addActionListener((e) -> {
            JOptionPane.showMessageDialog(panel, "coming soon.",
                    "tip", JOptionPane.INFORMATION_MESSAGE);
        });
        forward.addActionListener((e) -> {
            JOptionPane.showMessageDialog(panel, "coming soon.",
                    "tip", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public void refreshTreeData(TreeNode node) {
        if (TreeNode.OBJECT.equals(node.type)) {
            JSONObject obj = (JSONObject) node.value;
            obj.clear();
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode currNode = (TreeNode) e.nextElement();
                obj.put(currNode.key, currNode.value);
                refreshTreeData(currNode);
            }
        } else if (TreeNode.ARRAY.equals(node.type)) {
            JSONArray array = (JSONArray) node.value;
            array.clear();
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode currNode = (TreeNode) e.nextElement();
                array.add(currNode.value);
                refreshTreeData(currNode);
            }
        }
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
            addItem(SelectItem.STRING);
            addItem(SelectItem.OBJECT);
            addItem(SelectItem.ARRAY);
            addItem(SelectItem.OTHER);
        }};

        private JBTextField key = new JBTextField();

        private JBLabel keyLabel = new JBLabel("key");

        private JBTextField value = new JBTextField();

        private JBLabel valueLabel = new JBLabel("value");

        private JButton ok = new JButton("ok");

        private JButton cancel = new JButton("cancel");

        private TreeNode selectNode;

        private TreeNode newNode = new TreeNode();

        private String title = "Add";

        private boolean isAdd;

        private Consumer<TreeNode> callback;

        public AddOrEdit(TreeNode node, boolean isAdd, Consumer<TreeNode> callback) {
            this.isAdd = isAdd;
            this.selectNode = node;
            this.callback = callback;
            if (isAdd) {
                if (TreeNode.ARRAY.equals(selectNode.type)) {
                    key.setVisible(false);
                    keyLabel.setVisible(false);
                }
            } else {
                title = "Edit";
                type.setSelectedItem(SelectItem.getItemByValue(node.type));
                if (TreeNode.OBJECT.equals(node.type) || TreeNode.ARRAY.equals(node.type)) {
                    value.setVisible(false);
                    valueLabel.setVisible(false);
                }
                TreeNode parent = (TreeNode) selectNode.getParent();
                if (parent != null && TreeNode.ARRAY.equals(parent.type)) {
                    key.setVisible(false);
                    keyLabel.setVisible(false);
                }
            }
            key.setText(isAdd ? "key" : node.key);
            value.setText(isAdd ? "value" : (node.value == null ? "null" : node.value.toString()));
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
                    if (TreeNode.OBJECT.equals(select.value) || TreeNode.ARRAY.equals(select.value)) {
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
                    TreeNode returnNode;
                    if (isAdd) {
                        newNode.key = key.getText();
                        newNode.type = ((SelectItem) type.getSelectedItem()).getValue();
                        if (TreeNode.OBJECT.equals(newNode.type)) {
                            newNode.value = new JSONObject();
                        } else if (TreeNode.ARRAY.equals(newNode.type)) {
                            newNode.value = new JSONArray();
                        } else if (TreeNode.STRING.equals(newNode.type)) {
                            newNode.value = value.getText();
                        } else {
                            String valueStr = value.getText();
                            try {
                                if ("".equals(valueStr)) {
                                    newNode.value = null;
                                } else if ("true".equalsIgnoreCase(valueStr) || "false".equalsIgnoreCase(valueStr)) {
                                    newNode.value = Boolean.parseBoolean(valueStr);
                                } else if (Utils.isInteger(valueStr)) {
                                    newNode.value = Long.parseLong(valueStr);
                                } else if (Utils.isFloat(valueStr)) {
                                    newNode.value = Double.parseDouble(valueStr);
                                } else {
                                    newNode.value = value.getText();
                                }
                            } catch (Exception ex) {
                                newNode.value = valueStr;
                            }
                        }
                        returnNode = newNode;
                    } else {
                        selectNode.key = key.getText();
                        selectNode.type = ((SelectItem) type.getSelectedItem()).getValue();
                        if (TreeNode.OBJECT.equals(selectNode.type)) {
                            JSONObject obj = new JSONObject();
                            for (int i = 0; i < selectNode.getChildCount(); i++) {
                                TreeNode child = (TreeNode) selectNode.getChildAt(i);
                                obj.put(child.key, child.value);
                            }
                            selectNode.value = obj;
                            selectNode.updateObjectNodeChildren();
                        } else if (TreeNode.ARRAY.equals(selectNode.type)) {
                            JSONArray array = new JSONArray();
                            for (int i = 0; i < selectNode.getChildCount(); i++) {
                                TreeNode child = (TreeNode) selectNode.getChildAt(i);
                                array.add(child.value);
                            }
                            selectNode.value = array;
                            selectNode.updateArrayNodeChildren();
                        } else if (TreeNode.STRING.equals(selectNode.type)) {
                            selectNode.value = value.getText();
                            while (true) {
                                Enumeration<?> ele = selectNode.children();
                                if (ele.hasMoreElements()) {
                                    treeModel.removeNodeFromParent((TreeNode) ele.nextElement());
                                } else {
                                    break;
                                }
                            }
                            selectNode.updateNode();
                        } else {
                            while (true) {
                                Enumeration<?> ele = selectNode.children();
                                if (ele.hasMoreElements()) {
                                    treeModel.removeNodeFromParent((TreeNode) ele.nextElement());
                                } else {
                                    break;
                                }
                            }
                            String valueStr = value.getText();
                            try {
                                if ("".equals(valueStr)) {
                                    selectNode.value = null;
                                } else if ("true".equalsIgnoreCase(valueStr) || "false".equalsIgnoreCase(valueStr)) {
                                    selectNode.value = Boolean.parseBoolean(valueStr);
                                } else if (Utils.isInteger(valueStr)) {
                                    selectNode.value = Long.parseLong(valueStr);
                                } else if (Utils.isFloat(valueStr)) {
                                    selectNode.value = Double.parseDouble(valueStr);
                                } else {
                                    selectNode.value = value.getText();
                                }
                            } catch (Exception ex) {
                                selectNode.value = valueStr;
                            }
                        }
                        returnNode = selectNode;
                    }
                    callback.accept(returnNode);
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
        OBJECT("Object", 1),
        ARRAY("Array", 2),
        STRING("String", 3),
        OTHER("Other", 4);

        private String name;

        private Integer value;

        SelectItem(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public static SelectItem getItemByValue(Integer value) {
            return Arrays.stream(values()).filter(item -> value.equals(item.getValue())).findAny().orElse(null);
        }

        public Integer getValue() {
            return value;
        }

        @Override
        public String toString() {
            return name;
        }
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
