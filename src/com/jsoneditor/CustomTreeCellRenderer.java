package com.jsoneditor;

import com.jsoneditor.node.ArrayNode;
import com.jsoneditor.node.DateNode;
import com.jsoneditor.node.ObjectNode;
import com.jsoneditor.node.TreeNode;
import icons.Icons;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/11 22:03
 */
public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        // 解决失去焦点背景色显示问题
        if (sel && !hasFocus) {
            setBackgroundSelectionColor(null);
        }

        // 解决value过长显示问题
        Dimension size = getPreferredSize();
        Dimension d = new Dimension(300, size.height);
        setPreferredSize(d);

        // icon
        TreeNode node = (TreeNode) value;
        Icon icon;
        if (node.filter) {
            // 搜索时，选中的节点
            icon = Icons.SELECT;
        } else {
            if (value instanceof ObjectNode) {
                icon = Icons.OBJECT;
            } else if (value instanceof ArrayNode) {
                icon = Icons.ARRAY;
            } else if (value instanceof DateNode) {
                icon = Icons.DATE;
            } else {
                icon = Icons.AUTO;
            }
        }
        setIcon(icon);
        return this;
    }
}
