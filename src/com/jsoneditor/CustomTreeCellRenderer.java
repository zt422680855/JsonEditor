package com.jsoneditor;

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
        setIcon(node.filter ? Icons.SELECT : node.icon());
        return this;
    }
}
