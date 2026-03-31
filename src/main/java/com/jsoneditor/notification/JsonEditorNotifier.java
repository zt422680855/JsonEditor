package com.jsoneditor.notification;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.notification.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.ScrollingModel;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/12 22:56
 */
public class JsonEditorNotifier {

    public static void info(String content) {
        notify(content, NotificationType.INFORMATION);
    }

    public static void warning(String content) {
        notify(content, NotificationType.WARNING);
    }

    public static void error(String content) {
        notify(content, NotificationType.ERROR);
    }

    private static void notify(String content, NotificationType notificationType) {
        // Notification notification = NOTIFICATION_GROUP.createNotification("JsonEditor Tip", content, notificationType, null);
        // notification.notify(null);

        NotificationGroupManager.getInstance()
                .getNotificationGroup("JsonEditorNotificationGroup")
                .createNotification("JsonEditor Tip", content, notificationType)
                .notify(null);
    }

    public static void hintInfo(Editor editor, String message) {
        if (message != null && !"".equals(message.trim())) {
            ScrollingModel scrollingModel = editor.getScrollingModel();
            scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE);
            HintManager.getInstance().showInformationHint(editor, message);
        }
    }

    public static void hintError(Editor editor, String message) {
        if (message != null && !message.trim().isEmpty()) {
            ScrollingModel scrollingModel = editor.getScrollingModel();
            scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE);
            HintManager.getInstance().showErrorHint(editor, message);
        }
    }
}
