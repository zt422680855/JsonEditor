package com.demo.trans.specifications;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/25 22:41
 */
public class JTreeTransferHandler extends TransferHandler {

    protected DnDJTree tree;
    private static final long serialVersionUID = -6851440217837011463L;

    /**
     * Creates a JTreeTransferHandler to handle a certain tree. Note that this
     * constructor does NOT set this transfer handler to be that tree's transfer
     * handler, you must still add it manually.
     *
     * @param tree
     */
    public JTreeTransferHandler(DnDJTree tree) {
        super();
        this.tree = tree;
    }

    /**
     * @param supp
     * @return
     */
    @Override
    public boolean canImport(TransferSupport supp) {
        if (supp.isDataFlavorSupported(DnDTreeList.DnDTreeList_FLAVOR)) {
            DnDNode[] destPaths = null;
            // get the destination paths
            if (supp.isDrop()) {
                TreePath dropPath = ((JTree.DropLocation) supp.getDropLocation()).getPath();
                if (dropPath == null) {
                    // debugging a few anomalies with dropPath being null.
                    System.out.println("Drop path somehow came out null");
                    return false;
                }
                if (dropPath.getLastPathComponent() instanceof DnDNode) {
                    destPaths = new DnDNode[1];
                    destPaths[0] = (DnDNode) dropPath.getLastPathComponent();
                }
            } else {
                // cut/copy, get all selected paths as potential drop paths
                TreePath[] paths = this.tree.getSelectionPaths();
                if (paths == null) {
                    // possibility no nodes were selected, do nothing
                    return false;
                }
                destPaths = new DnDNode[paths.length];
                for (int i = 0; i < paths.length; i++) {
                    destPaths[i] = (DnDNode) paths[i].getLastPathComponent();
                }
            }
            for (int i = 0; i < destPaths.length; i++) {
                // check all destinations accept all nodes being transfered
                DataFlavor[] incomingFlavors = supp.getDataFlavors();
                for (int j = 1; j < incomingFlavors.length; j++) {
                    if (!destPaths[i].canImport(incomingFlavors[j])) {
                        // found one unsupported import, invalidate whole import
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * @param c
     * @return null if no nodes were selected, or this transfer handler was not
     * added to a DnDJTree. I don't think it's possible because of the
     * constructor layout, but one more layer of safety doesn't matter.
     */
    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof DnDJTree) {
            ((DnDJTree) c).setSelectionPaths(((DnDJTree) c).getSelectionPaths());
            return new DnDTreeList(((DnDJTree) c).getSelectionPaths());
        } else {
            return null;
        }
    }

    /**
     * @param c
     * @param t
     * @param action
     */
    @Override
    protected void exportDone(JComponent c, Transferable t, int action) {
        if (action == TransferHandler.MOVE) {
            // we need to remove items imported from the appropriate source.
            try {
                // get back the list of items that were transfered
                ArrayList<TreePath> list = ((DnDTreeList) t
                        .getTransferData(DnDTreeList.DnDTreeList_FLAVOR)).getNodes();
                for (int i = 0; i < list.size(); i++) {
                    // get the source
                    DnDNode sourceNode = (DnDNode) list.get(i).getLastPathComponent();
                    DefaultTreeModel model = (DefaultTreeModel) this.tree.getModel();
                    model.removeNodeFromParent(sourceNode);
                }
            } catch (UnsupportedFlavorException exception) {
                // for debugging purposes (and to make the compiler happy). In
                // theory, this shouldn't be reached.
                exception.printStackTrace();
            } catch (IOException exception) {
                // for debugging purposes (and to make the compiler happy). In
                // theory, this shouldn't be reached.
                exception.printStackTrace();
            }
        }
    }

    /**
     * @param c
     * @return
     */
    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    /**
     * @param supp
     * @return
     */
    @Override
    public boolean importData(TransferSupport supp) {
        if (this.canImport(supp)) {
            try {
                // Fetch the data to transfer
                Transferable t = supp.getTransferable();
                ArrayList<TreePath> list;

                list = ((DnDTreeList) t.getTransferData(DnDTreeList.DnDTreeList_FLAVOR)).getNodes();

                TreePath[] destPaths;
                DefaultTreeModel model = (DefaultTreeModel) this.tree.getModel();
                if (supp.isDrop()) {
                    // the destination path is the location
                    destPaths = new TreePath[1];
                    destPaths[0] = ((javax.swing.JTree.DropLocation) supp.getDropLocation())
                            .getPath();
                } else {
                    // pasted, destination is all selected nodes
                    destPaths = this.tree.getSelectionPaths();
                }
                // create add events
                for (int i = 0; i < destPaths.length; i++) {
                    // process each destination
                    DnDNode destNode = (DnDNode) destPaths[i].getLastPathComponent();
                    for (int j = 0; j < list.size(); j++) {
                        // process each node to transfer
                        int destIndex = -1;
                        DnDNode sourceNode = (DnDNode) list.get(j).getLastPathComponent();
                        // case where we moved the node somewhere inside of the
                        // same node
                        boolean specialMove = false;
                        if (supp.isDrop()) {
                            // chance to drop to a determined location
                            destIndex = ((JTree.DropLocation) supp.getDropLocation())
                                    .getChildIndex();
                        }
                        if (destIndex == -1) {
                            // use the default drop location
                            destIndex = destNode.getAddIndex(sourceNode);
                        } else {
                            // update index for a determined location in case of
                            // any shift
                            destIndex += j;
                        }
                        model.insertNodeInto(sourceNode, destNode, destIndex);
                    }
                }
                return true;
            } catch (UnsupportedFlavorException exception) {
                // TODO Auto-generated catch block
                exception.printStackTrace();
            } catch (IOException exception) {
                // TODO Auto-generated catch block
                exception.printStackTrace();
            }
        }
        // import isn't allowed at this time.
        return false;
    }

}
