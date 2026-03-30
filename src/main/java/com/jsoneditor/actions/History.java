package com.jsoneditor.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import org.jetbrains.annotations.NotNull;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/8/24 22:26
 */
public class History extends AnAction {

    private JBPopupMenu contextMenus = new JBPopupMenu();
    private JBMenuItem tab1 = new JBMenuItem("tab 1", null);
    private JBMenuItem tab2 = new JBMenuItem("tab 2", null);
    private JBMenuItem tab3 = new JBMenuItem("tab 3", null);
    private JBMenuItem tab4 = new JBMenuItem("tab 4", null);
    private JBMenuItem tab5 = new JBMenuItem("tab 5", null);
    private JBMenuItem tab6 = new JBMenuItem("tab 6", null);
    private JBMenuItem tab7 = new JBMenuItem("tab 7", null);

    public History() {
        Presentation presentation = getTemplatePresentation();
        presentation.setIcon(AllIcons.General.History);
        presentation.setText("history");
        contextMenus.add(tab1);
        contextMenus.add(tab2);
        contextMenus.add(tab3);
        contextMenus.add(tab4);
        contextMenus.add(tab5);
        contextMenus.add(tab6);
        contextMenus.add(tab7);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // contextMenus.show(this, e.get);
        System.out.println(e.getPlace());
    }

}
