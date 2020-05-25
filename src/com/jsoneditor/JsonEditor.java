package com.jsoneditor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.*;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
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
    private JBTextArea textArea = new JBTextArea();
    private JButton format = new JButton("format");
    private JButton compressJson = new JButton("compress");

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
    private JBMenuItem addSub = new JBMenuItem("新增子节点");
    private JBMenuItem addSibling = new JBMenuItem("新增兄弟节点");
    private JBMenuItem edit = new JBMenuItem("编辑");
    private JBMenuItem delete = new JBMenuItem("删除");

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
                    treeModel.insertNodeInto(node, select, select.getChildCount());
                    if (TreeNode.ARRAY.equals(select.type)) {
                        node.updateArrayNode();
                    } else {
                        select.type = TreeNode.OBJECT;
                    }
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
                    treeModel.insertNodeInto(node, parent, parent.getIndex(select) + 1);
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
                    if (TreeNode.OBJECT.equals(node.type)) {
                        node.updateObjectNodeChildren();
                    } else if (TreeNode.ARRAY.equals(node.type)) {
                        node.updateArrayNodeChildren();
                    }
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
                    parent.updateArrayNodeChildren();
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
        c.weighty = 2;
        c.ipady = -10;
        c.fill = GridBagConstraints.BOTH;
        GridBagLayout leftLayout = new GridBagLayout();
        left.setLayout(leftLayout);
        leftLayout.setConstraints(format, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        leftLayout.setConstraints(compressJson, c);
        left.add(format);
        left.add(compressJson);
        c = new GridBagConstraints();
        c.weighty = 100;
        c.gridwidth = 2;
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
                TreePath pathWhenReleased = tree.getPathForLocation(e.getX(), e.getY());
                if (pathWhenReleased != null && movingPath != null) {
                    Optional.ofNullable(pathWhenReleased.getLastPathComponent()).ifPresent((curNode) -> {
                        TreeNode target = (TreeNode) curNode;
                        Optional.ofNullable(target.getParent()).ifPresent((parentNode) -> {
                            TreeNode parent = (TreeNode) parentNode;
                            TreeNode movingNode = (TreeNode) movingPath.getLastPathComponent();
                            TreeNode movingNodeParent = (TreeNode) movingNode.getParent();
                            int newIndex = parent.getIndex(target);
                            if (!parent.equals(movingNode.getParent()) || newIndex > parent.getIndex(movingNode)) {
                                newIndex += 1;
                            }
                            treeModel.insertNodeInto(movingNode.clone(), parent, newIndex);
                            treeModel.removeNodeFromParent(movingNode);
                            movingPath = null;
                            parent.updateNode();
                            movingNodeParent.updateNode();
                        });
                    });
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
            textArea.setText(JSON.toJSONString(json, true));
        });
        compressJson.addActionListener((e) -> {
            Object json = JSON.parse(textArea.getText(), Feature.OrderedField);
            textArea.setText(JSON.toJSONString(json));
        });
        syncToRight.addActionListener((e) -> {
            root.removeAllChildren();
            root.value = JSON.parse(textArea.getText(), Feature.OrderedField);
            root.loadTreeNodes();
            tree.expandPath(new TreePath(root.getPath()));
            tree.updateUI();
        });
        syncToLeft.addActionListener((e) -> {
            treeDataToJson(root);
            System.out.println(root.value);
            textArea.setText(JSON.toJSONString(root.value, true));
        });
        expendJson.addActionListener((e) -> expandTree(tree, new TreePath(root)));
        closeJson.addActionListener((e) -> collapseTree(tree, new TreePath(root)));
    }

    public void treeDataToJson(TreeNode node) {
        if (TreeNode.OBJECT.equals(node.type)) {
            JSONObject obj = new JSONObject(true);
            for (int i = 0; i < node.getChildCount(); i++) {
                TreeNode currNode = (TreeNode) node.getChildAt(i);
                obj.put(currNode.key, currNode.value);
                treeDataToJson(currNode);
            }
            node.value = obj;
        } else if (TreeNode.ARRAY.equals(node.type)) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < node.getChildCount(); i++) {
                TreeNode currNode = (TreeNode) node.getChildAt(i);
                array.add(currNode.value);
                treeDataToJson(currNode);
            }
            node.value = array;
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

        private JButton ok = new JButton("确定");

        private JButton cancel = new JButton("取消");

        private TreeNode selectNode;

        private TreeNode newNode = new TreeNode();

        private String title = "Add";

        private boolean isAdd;

        private Consumer<TreeNode> callback;

        public AddOrEdit(TreeNode node, boolean isAdd, Consumer<TreeNode> callback) {
            this.isAdd = isAdd;
            this.selectNode = node;
            this.callback = callback;
            if (!isAdd) {
                title = "Edit";
                type.setSelectedItem(SelectItem.getItemByValue(node.type));
            }
            key.setText(isAdd ? "key" : node.key);
            value.setText(isAdd ? "value" : node.value.toString());
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
                        } else {
                            newNode.value = value.getText();
                        }
                        returnNode = newNode;
                    } else {
                        selectNode.key = key.getText();
                        selectNode.type = ((SelectItem) type.getSelectedItem()).getValue();
                        if (TreeNode.OBJECT.equals(selectNode.type)) {
                            selectNode.value = new JSONObject();
                        } else if (TreeNode.ARRAY.equals(selectNode.type)) {
                            selectNode.value = new JSONArray();
                        } else {
                            selectNode.value = value.getText();
                        }
                        returnNode = selectNode;
                    }
                    returnNode.updateNode();
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
            "    \"name\": \"zhengt\",\n" +
            "    \"age\": 18,\n" +
            "    \"isHandsome\": true,\n" +
            "    \"email\": \"hj_zhengt@163.com\",\n" +
            "    \"address\": {\n" +
            "        \"country\": \"中国\",\n" +
            "        \"province\": \"江苏\",\n" +
            "        \"city\": \"南京\"\n" +
            "    },\n" +
            "    \"hobbys\": [\n" +
            "        {\n" +
            "            \"order\": \"first\",\n" +
            "            \"name\": \"coding\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"order\": \"second\",\n" +
            "            \"name\": \"fishing\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"order\": \"third\",\n" +
            "            \"name\": \"whatever\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";

}
