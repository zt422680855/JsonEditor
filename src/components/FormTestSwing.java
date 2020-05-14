package components;

import javax.swing.*;
import java.awt.*;

/**
 * @Description:
 * @Author: zhengtao
 * @CreateDate: 2020/4/27 22:26
 */
public class FormTestSwing {

    private JPanel north = new JPanel();

    private JPanel center = new JPanel();

    private JPanel south = new JPanel();

    //为了让位于底部的按钮可以拿到组件内容，这里把表单组件做成类属性
    private JLabel r1 = new JLabel("输出：");
    private JLabel r2 = new JLabel("NULL");

    private JLabel name = new JLabel("姓名：");
    private JTextField nameContent = new JTextField();

    private JLabel age = new JLabel("年龄：");
    private JTextField ageContent = new JTextField();

    public JPanel initNorth() {

        //定义表单的标题部分，放置到IDEA会话框的顶部位置

        JLabel title = new JLabel("叫你不要乱点");
        title.setFont(new Font("微软雅黑", Font.PLAIN, 26)); //字体样式
        title.setHorizontalAlignment(SwingConstants.CENTER); //水平居中
        title.setVerticalAlignment(SwingConstants.CENTER); //垂直居中
        north.add(title);

        return north;
    }

    public JPanel initCenter() {
        // 3行2列的表格布局
        center.setLayout(new GridLayout(3, 2));

//        r1.setForeground(new Color(255, 47, 93)); //设置字体颜色
//        center.add(r1);
//        r2.setForeground(new Color(139, 181, 20)); //设置字体颜色
//        center.add(r2);
//
//        center.add(name);
//        center.add(nameContent);
//
//        center.add(age);
//        center.add(ageContent);

        return center;
    }

    public JPanel initSouth() {
        JButton submit = new JButton("确定");
        submit.setHorizontalAlignment(SwingConstants.CENTER);
        submit.setVerticalAlignment(SwingConstants.CENTER);
        south.add(submit);

        //按钮事件绑定
        submit.addActionListener(e -> {
            String name = nameContent.getText();
            String age = ageContent.getText();
            r2.setText(String.format("name:%s, age:%s", name, age));
        });

        return south;
    }
}
