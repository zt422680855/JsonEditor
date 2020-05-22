package com.jsoneditor;

import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @Author: 19043204
 * @CreateDate: 2020/5/22 22:28
 */
public class TransTreeNodeList implements Transferable, Serializable {

    private static final long serialVersionUID = 11111111111111111L;

    public final static DataFlavor TRANSTREENODELIST_FLAVOR = new DataFlavor(TransTreeNodeList.class, "Drag and drop list");

    protected List<DataFlavor> flavors;

    protected List<TreePath> nodes;

    public TransTreeNodeList(List<TreePath> nodes) {
        initFlavors(nodes);
    }

    public TransTreeNodeList(TreePath[] nodes) {
        List<TreePath> n = new ArrayList<>(nodes.length);
        n.addAll(Arrays.asList(nodes));
        initFlavors(n);
    }

    private void initFlavors(List<TreePath> nodes) {
        this.nodes = nodes;
        flavors = new ArrayList<DataFlavor>(){{
            add(TransTreeNodeList.TRANSTREENODELIST_FLAVOR);
        }};
        for (int i = 0; i < nodes.size(); i++) {
            DataFlavor[] temp = ((TreeNode) nodes.get(i).getLastPathComponent()).getTransferDataFlavors();
            for (int j = 0; j < temp.length; j++) {
                if (!flavors.contains(temp[j])) {
                    flavors.add(temp[j]);
                }
            }
        }
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (isDataFlavorSupported(flavor)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] flavs = new DataFlavor[flavors.size()];
        return flavors.toArray(flavs);
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        DataFlavor[] flavs = getTransferDataFlavors();
        for (int i = 0; i < flavs.length; i++) {
            if (flavs[i].equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    public List<TreePath> getNodes() {
        return this.nodes;
    }

}
