package com.demo.trans.specifications;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/25 22:38
 */
public class DnDJTree extends JTree implements MouseListener, PropertyChangeListener,
        TreeModelListener, ActionListener {
    private static final long serialVersionUID = -4260543969175732269L;
    protected Node<AWTEvent> undoLoc;

    private boolean undoStack;
    private boolean doingUndo;

    /**
     * Constructs a DnDJTree with root as the main node.
     *
     * @param root
     */
    public DnDJTree(DnDNode root) {
        super();
        this.setModel(new DnDTreeModel(root));
        // turn on the JComponent dnd interface
        this.setDragEnabled(true);
        // setup our transfer handler
        this.setTransferHandler(new JTreeTransferHandler(this));
        this.setDropMode(DropMode.ON_OR_INSERT);
        this.setScrollsOnExpand(true);
        // this.addTreeSelectionListener(this);
        this.addMouseListener(this);
        this.getModel().addTreeModelListener(this);
        this.undoLoc = new Node<AWTEvent>(null);
    }

    /**
     * @param e
     */
    protected void addUndo(AWTEvent e) {
        this.undoLoc.linkNext(new Node<AWTEvent>(e));
        this.undoLoc = this.undoLoc.next;
    }

    /**
     * Only returns the top level selection<br>
     * ex. if a child and it's parent are selected, only it's parent is returned
     * in the list.
     *
     * @return an array of TreePath objects indicating the selected nodes, or
     * null if nothing is currently selected
     */
    @Override
    public TreePath[] getSelectionPaths() {
        // get all selected paths
        TreePath[] temp = super.getSelectionPaths();
        if (temp != null) {
            ArrayList<TreePath> list = new ArrayList<TreePath>();
            for (int i = 0; i < temp.length; i++) {
                // determine if a node can be added
                boolean canAdd = true;
                for (int j = 0; j < list.size(); j++) {
                    if (temp[i].isDescendant(list.get(j))) {
                        // child was a descendant of another selected node,
                        // disallow add
                        canAdd = false;
                        break;
                    }
                }
                if (canAdd) {
                    list.add(temp[i]);
                }
            }
            return list.toArray(new TreePath[list.size()]);
        } else {
            // no paths selected
            return null;
        }
    }

    /**
     * Implemented a check to make sure that it is possible to de-select all
     * nodes. If this component is added as a mouse listener of another
     * component, that componenet can trigger a deselect of all nodes.
     * <p>
     * This method also allows for de-select if a blank spot inside this tree is
     * selected. Note that using the expand/contract button next to the label
     * will not cause a de-select.
     * <p>
     * if the given mouse event was from a popup trigger, was not BUTTON1, or
     * shift/control were pressed, a deselect is not triggered.
     *
     * @param e
     **/
    @Override
    public void mouseClicked(MouseEvent e) {
        if (!e.isPopupTrigger() && e.getButton() == MouseEvent.BUTTON1 && !e.isShiftDown() && !e
                .isControlDown()) {
            if (e.getSource() != this) {
                // source was elsewhere, deselect
                this.clearSelection();
            } else {
                // get the potential selection bounds
                Rectangle bounds = this.getRowBounds(this.getClosestRowForLocation(e.getX(), e
                        .getY()));
                if (!bounds.contains(e.getPoint())) {
                    // need to check to see if the expand box was clicked
                    Rectangle check = new Rectangle(bounds.x - 15, bounds.y, 9, bounds.height);
                    if (!check.contains(e.getPoint())) {
                        this.clearSelection();
                    }
                }
            }
        }
    }

    /**
     * @param e
     **/
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * @param e
     **/
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * @param e
     **/
    @Override
    public void mousePressed(MouseEvent e) {
    }

    /**
     * @param e
     **/
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    public void performRedo() {
        if (this.undoLoc.next != null) {
            this.undoLoc = this.undoLoc.next;
            if (this.undoLoc.data instanceof UndoEventCap) {
                // should be start cap. else, diagnostic output
                if (((UndoEventCap) this.undoLoc.data).isStart()) {
                    this.doingUndo = true;
                    this.undoLoc = this.undoLoc.next;
                    while (!(this.undoLoc.data instanceof UndoEventCap)) {
                        if (this.undoLoc.data instanceof TreeEvent) {
                            // perform the action
                            if (this.undoLoc.data instanceof TreeEvent) {
                                this.performTreeEvent(((TreeEvent) this.undoLoc.data));
                            }
                        }
                        this.undoLoc = this.undoLoc.next;
                    }
                    this.doingUndo = false;
                } else {
                    System.out.println("undo stack problems");
                }
            }
        }
    }

    public void performUndo() {
        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        if (this.undoLoc.data instanceof UndoEventCap) {
            // should be end cap. else, diagnostic output
            if (!((UndoEventCap) this.undoLoc.data).isStart()) {
                this.doingUndo = true;
                this.undoLoc = this.undoLoc.prev;
                while (!(this.undoLoc.data instanceof UndoEventCap)) {
                    if (this.undoLoc.data instanceof TreeEvent) {
                        // perform inverse
                        // System.out.println(((AddRemoveEvent)
                        // this.undoLoc.data).invert());
                        this.performTreeEvent(((TreeEvent) this.undoLoc.data).invert());
                    }
                    this.undoLoc = this.undoLoc.prev;
                }
                // move to previous
                if (this.undoLoc.prev != null) {
                    this.undoLoc = this.undoLoc.prev;
                }
                this.doingUndo = false;
            } else {
                System.out.println("undo stack problems");
            }
        }
    }

    public void performTreeEvent(TreeEvent e) {
        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        if (e.isAdd()) {
            model.insertNodeInto(e.getNode(), e.getDestination(), e.getIndex());
        } else {
            model.removeNodeFromParent(e.getNode());
        }
    }

    /**
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DeleteAction.DO_DELETE)) {
            // perform delete
            DefaultTreeModel model = (DefaultTreeModel) this.getModel();
            TreePath[] selection = this.getSelectionPaths();
            if (selection != null) {
                // something is selected, delete it
                for (int i = 0; i < selection.length; i++) {
                    if (((DnDNode) selection[i].getLastPathComponent()).getLevel() > 1) {
                        // TODO send out action to partially remove node
                        model.removeNodeFromParent((DnDNode) selection[i].getLastPathComponent());
                    }
                }
            }
        } else if (evt.getPropertyName().equals(UndoAction.DO_UNDO)) {
            this.performUndo();
        } else if (evt.getPropertyName().equals(RedoAction.DO_REDO)) {
            this.performRedo();
        } else {
            System.out.println(evt.getPropertyName());
        }
    }

    /**
     * @param e
     */
    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        // TODO Auto-generated method stub
        System.out.println("nodes changed");
    }

    /**
     * @param e
     */
    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        // TODO Auto-generated method stub
        if (!this.doingUndo) {
            this.checkUndoStatus();
            System.out.println("inserted");
            int index = e.getChildIndices()[0];
            DnDNode parent = (DnDNode) e.getTreePath().getLastPathComponent();
            this.addUndo(new TreeEvent(this, true, parent, (DnDNode) e.getChildren()[0], index));
        }
    }

    /**
     * @param e
     */
    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        // TODO Auto-generated method stub
        if (!this.doingUndo) {
            this.checkUndoStatus();
            System.out.println("removed");
            int index = e.getChildIndices()[0];
            DnDNode parent = (DnDNode) e.getTreePath().getLastPathComponent();
            this.addUndo(new TreeEvent(this, false, parent, (DnDNode) e.getChildren()[0], index));
        }
    }

    /**
     * @param e
     */
    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        // TODO Auto-generated method stub
        System.out.println("structure changed");
    }

    /**
     * @param e
     */
    protected void checkUndoStatus() {
        if (!this.undoStack) {
            this.undoStack = true;
            this.addUndo(new UndoEventCap(this, true));
            Timer timer = new Timer(100, this);
            timer.setRepeats(false);
            timer.setActionCommand("update");
            timer.start();
        }
    }

    /**
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("update") && this.undoStack) {
            this.undoStack = false;
            this.addUndo(new UndoEventCap(this, false));
        }
    }

}
