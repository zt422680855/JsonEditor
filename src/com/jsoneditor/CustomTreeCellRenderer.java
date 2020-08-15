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
        // 解决选中并失去焦点时，背景颜色不一致问题
        if (sel) {
            setBackgroundSelectionColor(null);
        }
        // 解决value过长显示问题
        final Dimension size = getPreferredSize();
        Dimension rv = new Dimension(300, size.height);
        setPreferredSize(rv);
        // 设置节点icon
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
