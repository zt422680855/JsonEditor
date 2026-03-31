package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/6/5 22:49
 */
public interface Icons {

    Icon TO_LEFT = IconLoader.getIcon("/icons/toLeft.png", Icons.class);
    Icon TO_RIGHT = IconLoader.getIcon("/icons/toRight.png", Icons.class);

    Icon ADD = IconLoader.getIcon("/icons/add.png", Icons.class);
    Icon EDIT = IconLoader.getIcon("/icons/edit.png", Icons.class);
    Icon DEL = IconLoader.getIcon("/icons/del.png", Icons.class);
    Icon COPY = IconLoader.getIcon("/icons/copy.png", Icons.class);
    Icon COPY_KEY = IconLoader.getIcon("/icons/key.png", Icons.class);
    Icon COPY_VALUE = IconLoader.getIcon("/icons/value.png", Icons.class);

    Icon OBJECT = IconLoader.getIcon("/icons/object.png", Icons.class);
    Icon ARRAY = IconLoader.getIcon("/icons/array.png", Icons.class);
    Icon AUTO = IconLoader.getIcon("/icons/auto.png", Icons.class);
    Icon DATE = IconLoader.getIcon("/icons/date.png", Icons.class);

    Icon FORMAT = IconLoader.getIcon("/icons/format.svg", Icons.class);
    Icon COMPRESS = IconLoader.getIcon("/icons/compress.png", Icons.class);

    Icon SELECT = IconLoader.getIcon("/icons/select.png", Icons.class);

    Icon SHOW = IconLoader.getIcon("/icons/show.png", Icons.class);
    Icon HIDE = IconLoader.getIcon("/icons/hide.png", Icons.class);

}
