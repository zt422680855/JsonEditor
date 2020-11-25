package com.jsoneditor.moddles;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;
import com.jsoneditor.TreeUtils;
import com.jsoneditor.Undo;
import com.jsoneditor.node.TreeNode;
import com.jsoneditor.notification.JsonEditorNotifier;
import icons.Icons;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 21:51
 */
public class Middle extends JsonEditorModdle {

    private int horizontalPointWhenmousePressed;

    private JButton syncToRight = new JButton() {{
        setIcon(Icons.TO_RIGHT);
        setBorderPainted(false);
    }};
    private JButton syncToLeft = new JButton() {{
        setIcon(Icons.TO_LEFT);
        setBorderPainted(false);
    }};

    public Middle(Project project, JsonEditorModdle parent) {
        super(project, parent);
        paint();
        dragEnable();
    }

    private void paint() {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        c.ipadx = -45;
        c.ipady = -5;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = JBUI.insets(10, 0, 10, 0);
        layout.setConstraints(syncToRight, c);
        layout.setConstraints(syncToLeft, c);
        add(syncToRight);
        add(syncToLeft);
    }

    private void dragEnable() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                horizontalPointWhenmousePressed = e.getX();
            }

        });
        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                if (e.getX() > 5 && e.getX() < getWidth() - 5) {
                    setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Left left = ModdleContext.getLeft(project);
                Middle middle = ModdleContext.getMiddle(project);
                Right right = ModdleContext.getRight(project);

                int x = e.getX();
                int offset = x - horizontalPointWhenmousePressed;
                Dimension leftSize = left.getSize();
                int leftWidth = leftSize.width + offset;
                if (leftWidth > 0 && leftWidth + middle.getWidth() < parent.getWidth()) {
                    // 调整left大小
                    left.setSize(leftWidth, leftSize.height);
                    // 调整middle位置
                    middle.setLocation(leftWidth, 0);
                    // 调整right大小和位置
                    Dimension rightSize = right.getSize();
                    int rightWidth = rightSize.width - offset;
                    right.setSize(rightWidth, rightSize.height);
                    right.setLocation(leftWidth + middle.getWidth(), 0);

                    parent.updateUI();
                }
            }

        });
    }

    public void addListener() {
        syncToRight.addActionListener((e) -> {
            try {
                TreeNode root;
                Object parse = JSON.parse(ModdleContext.getText(project), Feature.OrderedField);
                root = TreeUtils.getNode("ROOT", parse);
                ModdleContext.setRoot(project, root);
                TreeUtils.refreshTree(root);
                root.updateNode();
                ModdleContext.expandNode(project, new TreePath(root.getPath()));
                ModdleContext.updateTree(project);
                Undo.clear(project);
            } catch (Exception ex) {
                JsonEditorNotifier.error("JSON format error.");
            }
        });
        syncToRight.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
        syncToLeft.addActionListener((e) -> {
            TreeNode root = ModdleContext.getRoot(project);
            TreeUtils.refreshJson(root);
            ModdleContext.setText(project, JSON.toJSONString(root.getValue(), SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
        });
        syncToLeft.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
    }

    public void toRight() {
        syncToRight.doClick();
    }

    public void toLeft() {
        syncToLeft.doClick();
    }

}
