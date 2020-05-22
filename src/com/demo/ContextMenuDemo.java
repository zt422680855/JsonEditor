package com.demo;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/15 22:37
 */
public class ContextMenuDemo extends JFrame implements MouseListener {

    private JTree tree;

    private JPopupMenu popMenu;

    private JMenuItem addItem;
    private JMenuItem delItem;
    private JMenuItem editItem;

    public ContextMenuDemo() {
        tree = new JTree();
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addMouseListener(this);
        tree.setCellEditor(new DefaultTreeCellEditor(tree, new DefaultTreeCellRenderer()));
        getContentPane().add(tree);
        setSize(200, 200);

        //添加菜单项以及为菜单项添加事件
        popMenu = new JPopupMenu();

        addItem = new JMenuItem("添加");
        addItem.addMouseListener(this);

        delItem = new JMenuItem("删除");
        delItem.addMouseListener(this);

        editItem = new JMenuItem("修改");
        editItem.addMouseListener(this);

        popMenu.add(addItem);
        popMenu.add(delItem);
        popMenu.add(editItem);

        getContentPane().add(new JScrollPane(tree));
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        if (path == null) {
            return;
        }
        tree.setSelectionPath(path);
        if (e.isMetaDown()) {
            popMenu.show(tree, e.getX(), e.getY());
        }
    }

    public void mouseReleased(MouseEvent e) {

    }

    //弹出菜单的事件处理程序（需要实现ActionListener接口)
    public void actionPerformed(ActionEvent e) {
        //获得右键选中的节点
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (e.getSource() == addItem) {
            ((DefaultTreeModel) tree.getModel()).insertNodeInto(
                    new DefaultMutableTreeNode("Test"), node, node.getChildCount());
            tree.expandPath(tree.getSelectionPath());
        } else if (e.getSource() == delItem) {
            if (node.isRoot()) {
                return;
            }
            ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(node);
        } else if (e.getSource() == editItem) {
            tree.startEditingAtPath(tree.getSelectionPath());
        }

    }

    public static void main(String[] args) {
        ContextMenuDemo frame = new ContextMenuDemo();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
}
