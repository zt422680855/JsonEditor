package com.demo.trans.specifications;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/25 22:51
 */
public class DeleteAction extends AbstractAction {
    public final static String DO_DELETE = "delete";

    public DeleteAction(String text) {
        super(text);
        // super(text, FindAction.ICON);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("DELETE"));
        this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_DELETE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.firePropertyChange(DeleteAction.DO_DELETE, null, null);
    }
}
