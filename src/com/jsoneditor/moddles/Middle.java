package com.jsoneditor.moddles;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.util.ui.JBUI;
import com.jsoneditor.TreeUtils;
import com.jsoneditor.Undo;
import com.jsoneditor.node.TreeNode;
import com.jsoneditor.notification.JsonEditorNotifier;
import icons.Icons;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/17 21:51
 */
public class Middle extends JsonEditorModdle {

    private JsonEditorModdle parent;

    private JButton syncToRight = new JButton() {{
        setIcon(Icons.TO_RIGHT);
        setBorderPainted(false);
    }};
    private JButton syncToLeft = new JButton() {{
        setIcon(Icons.TO_LEFT);
        setBorderPainted(false);
    }};

    public Middle(JsonEditorModdle parent) {
        this.parent = parent;
        paint();
    }

    private void paint() {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagLayout parentLayout = (GridBagLayout) parent.getLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 5;
        c.fill = GridBagConstraints.BOTH;
        parentLayout.setConstraints(this, c);
        parent.add(this);
        c = new GridBagConstraints();
        c.ipadx = -35;
        c.ipady = -1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = JBUI.insets(10, 0, 10, 0);
        layout.setConstraints(syncToRight, c);
        layout.setConstraints(syncToLeft, c);
        add(syncToRight);
        add(syncToLeft);
    }

    public void addListener() {
        syncToRight.addActionListener((e) -> {
            try {
                TreeNode root;
                Object parse = JSON.parse(ModdleContext.getText(), Feature.OrderedField);
                root = TreeNode.getNode("ROOT", parse);
                ModdleContext.setRoot(root);
                TreeUtils.refreshTree(root);
                ModdleContext.expandNode(new TreePath(root.getPath()));
                ModdleContext.updateTree();
                Undo.clear();
            } catch (Exception ex) {
                JsonEditorNotifier.error("JSON format error.");
            }
        });
        syncToLeft.addActionListener((e) -> {
            TreeNode root = ModdleContext.getRoot();
            TreeUtils.refreshJson(root);
            ModdleContext.setText(JSON.toJSONString(root.value, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
        });
    }

    public void toRight() {
        syncToRight.doClick();
    }

    public void toLeft() {
        syncToLeft.doClick();
    }

}
