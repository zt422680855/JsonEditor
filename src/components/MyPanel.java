package components;

import javax.swing.*;
import java.awt.*;

/**
 * @Description:
 * @Author: zhengtao
 * @CreateDate: 2020/4/27 22:26
 */
public class MyPanel {

    private JPanel north = new JPanel();

    private JPanel center = new JPanel();

    private JPanel south = new JPanel();

    public JPanel initNorth() {

        JLabel title = new JLabel("叫你不要乱点");
        title.setFont(new Font("微软雅黑", Font.PLAIN, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        north.add(title);

        return north;
    }

    public JPanel initCenter() {
        center.setLayout(new GridLayout(3, 2));
        return center;
    }

    public JPanel initSouth() {
        return south;
    }
}
