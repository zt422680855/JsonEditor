import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
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
    private PatchedDefaultMutableTreeNode root = new PatchedDefaultMutableTreeNode(new JSONObject() {{
        put("key", "ROOT");
    }});

    private JsonFormatter jsonFormatter = new JsonFormatter();

    public JsonEditor() {
        panel.setLayout(layout);
//        initArrow();
        paintLeft();
        paintMiddle();
        paintRight();
        addActions();
    }

    private void initArrow() {
        ImageIcon right = new ImageIcon(".\\src\\icons\\right.png");
        Image image = right.getImage();
        image.getScaledInstance(1, 1, Image.SCALE_DEFAULT);
        right.setImage(image);
        syncToRight.setIcon(right);
        ImageIcon left = new ImageIcon(".\\src\\icons\\left.png");
        image = left.getImage();
        image.getScaledInstance(1, 1, Image.SCALE_DEFAULT);
        left.setImage(image);
        syncToLeft.setIcon(left);
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
        tree.setCellRenderer(new TreeNodeRender());
        tree.expandPath(new TreePath(root.getPath()));
    }

    private void initTreeNodes(PatchedDefaultMutableTreeNode node, Object value) {
        if (value instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) value;
            jsonObject.forEach((k, v) -> {
                PatchedDefaultMutableTreeNode subNode = new PatchedDefaultMutableTreeNode(new JSONObject() {{
                    put("key", k);
                    put("value", v);
                }});
                node.add(subNode);
                initTreeNodes(subNode, v);
            });
        } else if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            for (int i = 0; i < array.size(); i++) {
                String k = i + "";
                Object v = array.get(i);
                PatchedDefaultMutableTreeNode subNode = new PatchedDefaultMutableTreeNode(new JSONObject() {{
                    put("key", k);
                    put("value", v);
                }});
                node.add(subNode);
                initTreeNodes(subNode, v);
            }
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
            JSONObject userObject = (JSONObject) root.getUserObject();
            Object jsonObject = JSON.parse(textArea.getText());
            userObject.put("value", jsonObject);
            initTreeNodes(root, jsonObject);
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
        PatchedDefaultMutableTreeNode node = (PatchedDefaultMutableTreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                PatchedDefaultMutableTreeNode n = (PatchedDefaultMutableTreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandTree(tree, path);
            }
        }
        tree.expandPath(parent);
    }

    private void collapseTree(Tree tree, TreePath parent) {
        PatchedDefaultMutableTreeNode node = (PatchedDefaultMutableTreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                PatchedDefaultMutableTreeNode n = (PatchedDefaultMutableTreeNode) e.nextElement();
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

    static class TreeNodeRender extends JBPanel implements TreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            PatchedDefaultMutableTreeNode node = (PatchedDefaultMutableTreeNode) value;
            Object obj = node.getUserObject();
            JSONObject userObject = (JSONObject) obj;
            String key = userObject.getString("key");
            Object data = userObject.get("value");
            String text;
            if (data instanceof JSONObject) {
                JSONObject object = (JSONObject) data;
                text = key + "{" + object.size() + "}";
            } else if (data instanceof JSONArray) {
                JSONArray array = (JSONArray) data;
                text = key + "[" + array.size() + "]";
            } else {
                text = key + ":" + (data != null ? data.toString() : "");
            }
            removeAll();
            GridBagLayout rendererLayout = new GridBagLayout();
            setLayout(rendererLayout);
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 10;
            JBLabel label = new JBLabel(text);
            rendererLayout.setConstraints(label, c);
            add(label);
            return this;
        }
    }

    public static void main(String[] args) {
        JsonEditor jsonEditor = new JsonEditor();
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setTitle("JsonEditor");
        jFrame.setSize(800, 500);
        jFrame.add(jsonEditor.getPanel());
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    public JBPanel getPanel() {
        return panel;
    }

}
