package com.jsoneditor;

import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * @Description: https://blog.csdn.net/expleeve/article/details/7764049
 * @Author: zhengt
 * @CreateDate: 2020/5/22 21:56
 */
public class JsonTreeTransferHandler extends TransferHandler {

    public JsonTreeTransferHandler() {

    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Nullable
    @Override
    protected Transferable createTransferable(JComponent c) {
        Tree tree = (Tree) c;
        TreePath[] paths = tree.getSelectionPaths();

        return null;
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        return super.canImport(comp, transferFlavors);
    }

    @Override
    public boolean importData(TransferSupport support) {

        return super.importData(support);
    }
}
