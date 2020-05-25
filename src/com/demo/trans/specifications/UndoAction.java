package com.demo.trans.specifications;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/25 22:51
 */
public class UndoAction extends AbstractAction {
    public final static Icon ICON = new ImageIcon("resources/undo.gif");
    public static final String DO_UNDO = "undo";

    public UndoAction(String text) {
        super(text, UndoAction.ICON);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Z"));
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Z);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.firePropertyChange(UndoAction.DO_UNDO, null, null);
    }

}
