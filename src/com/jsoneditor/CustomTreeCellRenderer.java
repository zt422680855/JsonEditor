package com.jsoneditor;

import com.intellij.ui.JBColor;
import com.intellij.ui.JBDefaultTreeCellRenderer;
import com.jsoneditor.node.ArrayNode;
import com.jsoneditor.node.ObjectNode;
import icons.Icons;

import javax.swing.*;
import java.awt.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/11 22:03
 */
public class CustomTreeCellRenderer extends JBDefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        // 解决失去焦点背景色显示问题
        if (sel && !hasFocus) {
            setBackgroundSelectionColor(null);
        }

        if (value instanceof TreeNode) {
            TreeNode node = (TreeNode) value;
            if (node.filter) {
                if (!node.getUserObject().startsWith("SELECT-")) {
                    node.setUserObject("SELECT-" + node.getUserObject());
                }
                setTextNonSelectionColor(JBColor.RED);
            } else {
                String[] split = node.getUserObject().split("-");
                node.setUserObject(split.length == 2 ? split[1] : split[0]);
                setTextNonSelectionColor(JBColor.BLACK);
            }
        }

        // 解决value过长显示问题
        Dimension size = getPreferredSize();
        Dimension d = new Dimension(300, size.height);
        setPreferredSize(d);
        Icon icon;
        if (value instanceof ObjectNode) {
            icon = Icons.OBJECT;
        } else if (value instanceof ArrayNode) {
            icon = Icons.ARRAY;
        } else {
            icon = Icons.AUTO;
        }
        setIcon(icon);
        return this;
    }
}
