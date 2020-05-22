import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import components.MyPluginDialog;

/**
 * @Description:
 * @Author: zhengtao
 * @CreateDate: 2020/4/27 22:40
 */
public class MyPluginAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        MyPluginDialog formTestDialog = new MyPluginDialog(e.getProject());
        formTestDialog.setResizable(true);
        formTestDialog.show();
    }
}
