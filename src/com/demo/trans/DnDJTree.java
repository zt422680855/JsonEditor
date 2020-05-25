package com.demo.trans;

import com.intellij.ui.treeStructure.Tree;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/25 22:25
 */
public class DnDJTree extends Tree implements TreeSelectionListener {
    private static final long serialVersionUID = -4260543969175732269L;
    /**
     * Used to keep track of selected nodes. This list is automatically updated.
     */
    protected ArrayList<TreePath> selected;

    /**
     * Constructs a DnDJTree with root as the main node.
     *
     * @param root
     */
    public DnDJTree(TreeNode root) {
        super(root);
        // turn on the JComponent dnd interface
        this.setDragEnabled(true);
        // setup our transfer handler
        this.setTransferHandler(new JTreeTransferHandler(this));
        // setup tracking of selected nodes
        this.selected = new ArrayList<TreePath>();
        this.addTreeSelectionListener(this);
    }

    /**
     * @return the list of selected nodes
     */
    @SuppressWarnings("unchecked")
    public ArrayList<TreePath> getSelection() {
        return (ArrayList<TreePath>) (this.selected).clone();
    }

    /**
     * keeps the list of selected nodes up to date.
     *
     * @param e
     **/
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        // should contain all the nodes who's state (selected/non selected)
        // changed
        TreePath[] selection = e.getPaths();
        for (int i = 0; i < selection.length; i++) {
            // for each node who's state changed, either add or remove it form
            // the selected nodes
            if (e.isAddedPath(selection[i])) {
                // node was selected
                this.selected.add(selection[i]);
            } else {
                // node was de-selected
                this.selected.remove(selection[i]);
            }
        }
    }

}
