package components;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @Description:
 * @Author: 19043204
 * @CreateDate: 2020/4/27 20:25
 */
public class MyPluginDialog extends DialogWrapper {

    private String projectName;

    private FormTestSwing formTestSwing = new FormTestSwing();

    public MyPluginDialog(@Nullable Project project) {
        super(project);
        setTitle("不要乱点~~");
        this.projectName = project.getName();
        init();
    }

    // 不需要展示时要重写返回null，否则IDEA将展示默认的"Cancel"和"OK"按钮
    @Override
    protected JComponent createNorthPanel() {
        return formTestSwing.initNorth();
    }

    @Override
    protected JComponent createSouthPanel() {
        return formTestSwing.initSouth();
    }

    @Override
    protected JComponent createCenterPanel() {
        return formTestSwing.initCenter();
    }

}
