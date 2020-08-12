package com.jsoneditor;

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
