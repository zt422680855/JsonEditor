package com.demo.trans.specifications;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/25 22:52
 */
public class RedoAction extends AbstractAction {
    public final static Icon ICON = new ImageIcon("resources/redo.gif");
    public static final String DO_REDO = "redo";

    public RedoAction(String text) {
        super(text, RedoAction.ICON);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Y"));
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.firePropertyChange(RedoAction.DO_REDO, null, null);
    }
}
