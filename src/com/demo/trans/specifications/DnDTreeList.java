package com.demo.trans.specifications;

import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/25 22:48
 */
public class DnDTreeList implements Transferable, Serializable {
    public final static DataFlavor DnDTreeList_FLAVOR = new DataFlavor(DnDTreeList.class,
            "Drag and drop list");
    /**
     *
     */
    private static final long serialVersionUID = 1270874212613332692L;
    /**
     * List of flavors this DnDTreeList can be retrieved as. Currently only
     * supports DnDTreeList_FLAVOR
     */
    protected ArrayList<DataFlavor> flavors;
    // protected DataFlavor[] flavors = { DnDTreeList.DnDTreeList_FLAVOR };

    /**
     * Nodes to transfer
     */
    protected ArrayList<TreePath> nodes;

    /**
     * @param selection
     */
    public DnDTreeList(ArrayList<TreePath> nodes) {
        this.finishBuild(nodes);
    }

    /**
     * @param selectionPaths
     */
    public DnDTreeList(TreePath[] nodes) {
        ArrayList<TreePath> n = new ArrayList<TreePath>(nodes.length);
        for (int i = 0; i < nodes.length; i++) {
            n.add(nodes[i]);
        }
        this.finishBuild(n);
    }

    /**
     * Called from contructors to finish building this object once data has been
     * put into the correct form.
     *
     * @param nodes
     */
    private void finishBuild(ArrayList<TreePath> nodes) {
        this.nodes = nodes;
        this.flavors = new ArrayList<DataFlavor>();
        this.flavors.add(DnDTreeList.DnDTreeList_FLAVOR);
        for (int i = 0; i < nodes.size(); i++) {
            // add a list of all flavors of selected nodes
            DataFlavor[] temp = ((DnDNode) nodes.get(i).getLastPathComponent())
                    .getTransferDataFlavors();
            for (int j = 0; j < temp.length; j++) {
                if (!this.flavors.contains(temp[j])) {
                    this.flavors.add(temp[j]);
                }
            }
        }
    }

    public ArrayList<TreePath> getNodes() {
        return this.nodes;
    }

    /**
     * @param flavor
     * @return
     * @throws UnsupportedFlavorException
     * @throws IOException
     **/
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (this.isDataFlavorSupported(flavor)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /**
     * @return
     **/
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        // TODO Auto-generated method stub
        DataFlavor[] flavs = new DataFlavor[this.flavors.size()];
        this.flavors.toArray(flavs);
        return flavs;
    }

    /**
     * @param flavor
     * @return
     **/
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        DataFlavor[] flavs = this.getTransferDataFlavors();
        for (int i = 0; i < flavs.length; i++) {
            if (flavs[i].equals(flavor)) {
                return true;
            }
        }
        return false;
    }

}
