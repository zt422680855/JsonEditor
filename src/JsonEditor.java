import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @Description:
 * @Author: 19043204
 * @CreateDate: 2020/5/7 17:44
 */
public class JsonEditor implements ToolWindowFactory {

    private JPanel panel = new JBPanel();
    private GridBagLayout layout = new GridBagLayout();

    // left
    private JTextArea textArea = new JTextArea();
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
    private PatchedDefaultMutableTreeNode root = new PatchedDefaultMutableTreeNode("root");

    private JsonFormatter jsonFormatter = new JsonFormatter();

    public JsonEditor() {
        panel.setLayout(layout);
//        initArrow();
//        panel.setBackground(JBColor.GRAY);
        initLeft();
        initMiddle();
        tree = new Tree(root);
        tree.setEditable(true);
        initRight();

        this.format.addActionListener((e) -> {
            String text = this.textArea.getText();
            text = text.replaceAll("(\\n)", "");
            try {
                text = (new JSONObject(text)).toString();
            } catch (JSONException ex) {
            }
            String formattedText = this.jsonFormatter.format(text);
            this.textArea.setText(formattedText);
        });
        this.compressJson.addActionListener((e) -> {
            String text = this.textArea.getText();
            this.textArea.setText(text.replaceAll("(\\n)", ""));
        });
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

    private void initLeft() {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 150;
        c.weighty = 10;
        c.fill = GridBagConstraints.BOTH;
        JPanel left = new JBPanel();
//        left.setBackground(JBColor.PINK);
        layout.setConstraints(left, c);
        panel.add(left);
        c = new GridBagConstraints();
        c.weighty = 2;
        c.weightx = 10;
        c.ipady = -10;
        c.fill = GridBagConstraints.BOTH;
        GridBagLayout leftLayout = new GridBagLayout();
        left.setLayout(leftLayout);
        leftLayout.setConstraints(format, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        leftLayout.setConstraints(compressJson, c);
        c = new GridBagConstraints();
        c.weighty = 100;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JBScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftLayout.setConstraints(scrollPane, c);
        left.add(format);
        left.add(compressJson);
        left.add(scrollPane);
    }

    private void initMiddle() {
        JPanel middle = new JBPanel();
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

    private void initRight() {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 100;
        c.weighty = 10;
        c.fill = GridBagConstraints.BOTH;
        JPanel right = new JBPanel();
//        right.setBackground(JBColor.GREEN);
        layout.setConstraints(right, c);
        panel.add(right);
        GridBagLayout rightLayout = new GridBagLayout();
        right.setLayout(rightLayout);
        c = new GridBagConstraints();
        c.weightx = 10;
        c.weighty = 2;
        c.ipady = -10;
        c.fill = GridBagConstraints.BOTH;
        rightLayout.setConstraints(expendJson, c);
        rightLayout.setConstraints(closeJson, c);
        rightLayout.setConstraints(back, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        rightLayout.setConstraints(forward, c);
        right.add(expendJson);
        right.add(closeJson);
        right.add(back);
        right.add(forward);
        JScrollPane scrollPane = new JBScrollPane();
        c = new GridBagConstraints();
        c.weighty = 100;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.BOTH;
        rightLayout.setConstraints(scrollPane, c);
        right.add(scrollPane);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(this.panel, "", false);
        toolWindow.getContentManager().addContent(content);
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

    public JPanel getPanel() {
        return panel;
    }

}
