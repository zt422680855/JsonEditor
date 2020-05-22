package components;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @Description:
 * @Author: zhengtao
 * @CreateDate: 2020/4/27 22:25
 */
public class MyPluginDialog extends DialogWrapper {

    private String projectName;

    private MyPanel myPanel = new MyPanel();

    public MyPluginDialog(@Nullable Project project) {
        super(project);
        setTitle("不要乱点~~");
        this.projectName = project.getName();
        init();
    }

    @Override
    protected JComponent createNorthPanel() {
        return myPanel.initNorth();
    }

    @Override
    protected JComponent createSouthPanel() {
        return myPanel.initSouth();
    }

    @Override
    protected JComponent createCenterPanel() {
        return myPanel.initCenter();
    }

}
