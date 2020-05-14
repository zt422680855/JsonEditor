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
        //是否允许用户通过拖拽的方式扩大或缩小
        formTestDialog.setResizable(true);
        formTestDialog.show();
    }
}
