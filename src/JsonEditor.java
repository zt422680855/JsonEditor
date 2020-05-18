import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.*;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

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
    private TreeNode root = new TreeNode("ROOT", "");

    private JBPopupMenu contextMenus = new JBPopupMenu();
    private JBMenuItem addSub = new JBMenuItem("新增子节点");
    private JBMenuItem addSibling = new JBMenuItem("新增兄弟节点");
    private JBMenuItem edit = new JBMenuItem("编辑");
    private JBMenuItem delete = new JBMenuItem("删除");

    private JsonFormatter jsonFormatter = new JsonFormatter();

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
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                TreeNode newSub;
                String k;
                Object v;
                if (TreeNode.ARRAY.equals(select.type)) {
                    k = String.valueOf(select.getChildCount());
                    v = "value";
                    ((JSONArray) select.value).add("value");
                } else {
                    k = "key";
                    v = "value";
                    if (TreeNode.OBJECT.equals(select.type)) {
                        ((JSONObject) select.value).put(k, v);
                    } else {
                        select.value = new JSONObject() {{
                            put(k, v);
                        }};
                        select.type = TreeNode.OBJECT;
                    }
                }
                newSub = new TreeNode(k, v);
                model.insertNodeInto(newSub, select, select.getChildCount());
                select.updateNode();
                JOptionPane.showInputDialog("Please input a value");
            }
        });
        addSibling.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreeNode select = (TreeNode) tree.getLastSelectedPathComponent();
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                TreeNode parent = (TreeNode) select.getParent();
                if (parent != null) {
                    TreeNode sibling;
                    String k;
                    Object v;
                    if (TreeNode.ARRAY.equals(parent.type)) {
                        k = String.valueOf(parent.getIndex(select) + 1);
                        v = "value";
                        ((JSONArray) parent.value).add("value");
                    } else {
                        k = "key";
                        v = "value";
                        if (TreeNode.OBJECT.equals(parent.type)) {
                            ((JSONObject) parent.value).put(k, v);
                        }
                    }
                    sibling = new TreeNode(k, v);
                    model.insertNodeInto(sibling, parent, parent.getIndex(select) + 1);
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
        tree.setRowHeight(30);
        tree.expandPath(new TreePath(root.getPath()));
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isMetaDown()) {
                    contextMenus.show(tree, e.getX(), e.getY());
                }
            }
        });
    }

    private void loadTreeNodes(TreeNode parent) {
        Object value = parent.value;
        if (value instanceof JSONObject) {
            parent.type = TreeNode.OBJECT;
            JSONObject jsonObject = (JSONObject) value;
            jsonObject.forEach((k, v) -> {
                TreeNode subNode = new TreeNode(k, v);
                parent.add(subNode);
                loadTreeNodes(subNode);
            });
        } else if (value instanceof JSONArray) {
            parent.type = TreeNode.ARRAY;
            JSONArray array = (JSONArray) value;
            for (int i = 0; i < array.size(); i++) {
                String k = i + "";
                Object v = array.get(i);
                TreeNode subNode = new TreeNode(k, v);
                parent.add(subNode);
                loadTreeNodes(subNode);
            }
        } else if (value instanceof String) {
            parent.type = TreeNode.STRING;
        } else {
            parent.type = TreeNode.OTHER;
        }
    }

    private void addActions() {
        format.addActionListener((e) -> {
            String text = textArea.getText();
            text = text.replaceAll("(\\n)", "");
            String formattedText = jsonFormatter.format(text);
            textArea.setText(formattedText);
        });
        compressJson.addActionListener((e) -> {
            String text = textArea.getText();
            textArea.setText(text.replaceAll("(\\s*)", ""));
        });
        syncToRight.addActionListener((e) -> {
            root.removeAllChildren();
            root.value = JSON.parse(textArea.getText());
            loadTreeNodes(root);
            tree.expandPath(new TreePath(root.getPath()));
            tree.updateUI();
        });
        syncToLeft.addActionListener((e) -> {
            root.removeAllChildren();
            tree.updateUI();
        });
        expendJson.addActionListener((e) -> expandTree(tree, new TreePath(root)));
        closeJson.addActionListener((e) -> collapseTree(tree, new TreePath(root)));
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

    static class TreeNode extends PatchedDefaultMutableTreeNode {

        static final Integer OBJECT = 1;
        static final Integer ARRAY = 2;
        static final Integer STRING = 3;
        static final Integer OTHER = 4;

        private String key;

        private Object value;

        private String label;

        private Integer type = STRING;

        public TreeNode() {
        }

        public TreeNode(String key, Object value) {
            if (value instanceof JSONObject) {
                JSONObject object = (JSONObject) value;
                label = key + " : " + "{" + object.size() + "}";
            } else if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                label = key + " : " + "[" + array.size() + "]";
            } else {
                label = key + " : " + (value != null ? value.toString() : "");
            }
            setUserObject(label);
            this.key = key;
            this.value = value;
        }

        public TreeNode(Object userObject) {
            super(userObject);
        }

        private void updateNode() {
            if (value instanceof JSONObject) {
                label = key + " : " + "{" + getChildCount() + "}";
            } else if (value instanceof JSONArray) {
                label = key + " : " + "[" + getChildCount() + "]";
            } else {
                label = key + " : " + (value != null ? value.toString() : "");
            }
            setUserObject(label);
        }

    }

    static class AddOrEdit {

        private JBTextField key;

        private JBLabel keyLabel = new JBLabel("key");

        private JBTextField value;

        private JBLabel valueLabel = new JBLabel("value");

    }

    public static void main(String[] args) {
        JsonEditor jsonEditor = new JsonEditor();
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setTitle("JsonEditor");
        jFrame.setSize(800, 500);
        jFrame.add(jsonEditor.panel);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
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
