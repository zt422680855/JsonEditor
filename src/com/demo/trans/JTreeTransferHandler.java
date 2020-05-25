package com.demo.trans;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/25 22:28
 */
public class JTreeTransferHandler extends TransferHandler {
    protected DefaultTreeModel tree;
    /**
     *
     */
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
        this.tree = (DefaultTreeModel) tree.getModel();
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
     * @param c
     * @return null if no nodes were selected, or this transfer handler was not
     * added to a DnDJTree. I don't think it's possible because of the
     * constructor layout, but one more layer of safety doesn't matter.
     */
    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof DnDJTree) {
            return new DnDTreeList(((DnDJTree) c).getSelection());
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
                    // remove them
                    this.tree.removeNodeFromParent((DnDNode) list.get(i).getLastPathComponent());
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
     * @param supp
     * @return
     */
    @Override
    public boolean canImport(TransferSupport supp) {
        // Setup so we can always see what it is we are dropping onto.
        supp.setShowDropLocation(true);
        if (supp.isDataFlavorSupported(DnDTreeList.DnDTreeList_FLAVOR)) {
            // at the moment, only allow us to import list of DnDNodes

            // Fetch the drop path
            TreePath dropPath = ((JTree.DropLocation) supp.getDropLocation()).getPath();
            if (dropPath == null) {
                // Debugging a few anomalies with dropPath being null. In the
                // future hopefully this will get removed.
                System.out.println("Drop path somehow came out null");
                return false;
            }
            // Determine whether we accept the location
            if (dropPath.getLastPathComponent() instanceof DnDNode) {
                // only allow us to drop onto a DnDNode
                try {
                    // using the node-defined checker, see if that node will
                    // accept
                    // every selected node as a child.
                    DnDNode parent = (DnDNode) dropPath.getLastPathComponent();
                    ArrayList<TreePath> list = ((DnDTreeList) supp.getTransferable()
                            .getTransferData(DnDTreeList.DnDTreeList_FLAVOR)).getNodes();
                    for (int i = 0; i < list.size(); i++) {
                        if (parent.getAddIndex((DnDNode) list.get(i).getLastPathComponent()) < 0) {
                            return false;
                        }
                    }

                    return true;
                } catch (UnsupportedFlavorException exception) {
                    // Don't allow dropping of other data types. As of right
                    // now,
                    // only DnDNode_FLAVOR and DnDTreeList_FLAVOR are supported.
                    exception.printStackTrace();
                } catch (IOException exception) {
                    // to make the compiler happy.
                    exception.printStackTrace();
                }
            }
        }
        // something prevented this import from going forward
        return false;
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
                ArrayList<TreePath> list = ((DnDTreeList) t
                        .getTransferData(DnDTreeList.DnDTreeList_FLAVOR)).getNodes();
                // Fetch the drop location
                TreePath loc = ((javax.swing.JTree.DropLocation) supp.getDropLocation()).getPath();
                // Insert the data at this location
                for (int i = 0; i < list.size(); i++) {
                    this.tree.insertNodeInto((DnDNode) list.get(i).getLastPathComponent(),
                            (DnDNode) loc.getLastPathComponent(), ((DnDNode) loc
                                    .getLastPathComponent()).getAddIndex((DnDNode) list.get(i)
                                    .getLastPathComponent()));
                }
                // success!
                return true;
            } catch (UnsupportedFlavorException e) {
                // In theory, this shouldn't be reached because we already
                // checked to make sure imports were valid.
                e.printStackTrace();
            } catch (IOException e) {
                // In theory, this shouldn't be reached because we already
                // checked to make sure imports were valid.
                e.printStackTrace();
            }
        }
        // import isn't allowed at this time.
        return false;
    }

}
