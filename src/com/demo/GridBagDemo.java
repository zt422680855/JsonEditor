package com.demo;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

/**
 * @Description:
 * @Author: zhengtao
 * @CreateDate: 2020/5/11 21:17
 */
public class GridBagDemo {

    public static void main(String[] args) {
//        demo1();
        demo2();
    }

    private static void demo1() {
        JFrame jf = new JFrame("test");
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GridBagLayout gridBag = new GridBagLayout();    // 布局管理器
        GridBagConstraints c = null;                    // 约束

        JPanel panel = new JPanel(gridBag);

        JButton btn01 = new JButton("Button01");
        JButton btn02 = new JButton("Button02");
        JButton btn03 = new JButton("Button03");
        JButton btn04 = new JButton("Button04");
        JButton btn05 = new JButton("Button05");
        JButton btn06 = new JButton("Button06");
        JButton btn07 = new JButton("Button07");
        JButton btn08 = new JButton("Button08");
        JButton btn09 = new JButton("Button09");
        JButton btn10 = new JButton("Button10");

        /* 添加 组件 和 约束 到 布局管理器 */
        c = new GridBagConstraints();
        c.weighty = 50;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        JTextArea text = new JTextArea();
        gridBag.setConstraints(text, c);
        // Button01
        c = new GridBagConstraints();
        c.weighty = 10;
        gridBag.setConstraints(btn01, c); // 内部使用的仅是 c 的副本

        // Button02
        c = new GridBagConstraints();
        gridBag.setConstraints(btn02, c);

        // Button03
        c = new GridBagConstraints();
        gridBag.setConstraints(btn03, c);

        // Button04 显示区域占满当前行剩余空间（换行），组件填充显示区域
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(btn04, c);

        // Button05 显示区域独占一行（换行），组件填充显示区域
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(btn05, c);

        // Button06 显示区域占到当前尾倒车第二个单元格（下一个组件后需要手动换行），组件填充显示区域
        c = new GridBagConstraints();
        c.weighty = 10;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(btn06, c);

        // Button07 放置在当前行最后一个单元格（换行）
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridBag.setConstraints(btn07, c);

        // Button08 显示区域占两列，组件填充显示区域
        c = new GridBagConstraints();
        c.weighty = 20;
        c.gridheight = 2;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(btn08, c);

        // Button09 显示区域占满当前行剩余空间（换行），组件填充显示区域
        c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(btn09, c);

        // Button10 显示区域占满当前行剩余空间（换行），组件填充显示区域
        c = new GridBagConstraints();
        c.weighty = 10;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(btn10, c);

        /* 添加 组件 到 内容面板 */
        panel.add(text);
        panel.add(btn01);
        panel.add(btn02);
        panel.add(btn03);
        panel.add(btn04);
        panel.add(btn05);
        panel.add(btn06);
        panel.add(btn07);
        panel.add(btn08);
        panel.add(btn09);
        panel.add(btn10);

        jf.setContentPane(panel);
        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }

    private static void demo2() {
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GridBagLayout gr = new GridBagLayout();
        jFrame.setLayout(gr);
        Button bb1 = new Button("bb1");
        Button bb2 = new Button("bb2");
        Button bb3 = new Button("bb3");
        Button bb4 = new Button("bb4");
        Button bb5 = new Button("bb5");
        Button bb6 = new Button("bb6");
        Button bb7 = new Button("bb7");
        Button bb8 = new Button("bb8");
        GridBagConstraints gc = new GridBagConstraints();
        // 设置约束的fill参数,该参数表示当组件的大小小于网格单元的大小时在水平和垂直方向都填充
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 11;
        gc.weighty = 11;
        gr.setConstraints(bb1, gc);

        gc.weightx = 11;
        // 将下一个组件放置在坐标为1,1的位置。
        gc.gridx = 1;
        gc.gridy = 1;
        gr.setConstraints(bb2, gc);
        gc.weightx = 11;

        // 将下一个组件放置在坐标为2,1的位置。
        gc.gridx = 2;
        gc.gridy = 1;
        // 将下一个组件与网格单元的空白区域向外扩展10个像素,在这里可以看到网格包布局允许组件之间重叠。
        gc.insets = JBUI.insets(-10, -10, -10, -10);
        // 设置gridwidth参数的值为REMAINDER这样在后面使用该约束的组件将是该行的最后一个组件
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gr.setConstraints(bb3, gc);

        GridBagConstraints gc1 = new GridBagConstraints();
        gc1.weighty = 22;
        gc1.ipadx = 50;
        gc1.ipady = 50; // 将组件的最小尺寸加大ipadx*2个像素。
        gr.setConstraints(bb4, gc1);
        // 将以后的组件的最小尺寸设置为默认值,如果省掉该行,则以后组件的最小尺寸都会加大ipadx*2个像素。
        gc1.ipadx = 0;
        gc1.ipady = 0;
        gc1.anchor = GridBagConstraints.NORTHWEST; // 将下一个组件bb5的位置放置在单元网格的西北方向。
        // 因为bb5未设置fill,同时bb5设置了weightx(由gc参数设置)和weighty两个值以确定bb5所在的网格单元的大小，
        // 因而组件bb5的原始最小尺寸无法占据整个网格单元。
        gr.setConstraints(bb5, gc1);
        gc1.fill = GridBagConstraints.BOTH;
        gc1.gridwidth = GridBagConstraints.REMAINDER;
        gr.setConstraints(bb6, gc1);
        gc1.weighty = 33;
        // 使下一个组件bb7与网格单元之间在上，左，下，右，分别保持5,15,40,150个像素的空白位置。
        gc1.insets = JBUI.insets(5, 15, 40, 150);
        gr.setConstraints(bb7, gc1);
        gc1.weighty = 0;
        gc1.insets = JBUI.insets(0, 0, 0, 0); // 将insets的参数值设为默认值。
        gr.setConstraints(bb8, gc1);
        jFrame.setSize(500, 300);

        JTextArea text = new JTextArea("aaaaaaa");
        gr.setConstraints(text, gc1);

        jFrame.add(text);
        jFrame.add(bb1);
        jFrame.add(bb2);
        jFrame.add(bb3);
        jFrame.add(bb4);
        jFrame.add(bb5);
        jFrame.add(bb6);
        jFrame.add(bb7);
        jFrame.add(bb8);

        jFrame.setVisible(true);
    }

}
