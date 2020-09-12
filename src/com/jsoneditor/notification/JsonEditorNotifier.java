package com.jsoneditor.notification;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.ScrollingModel;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/9/12 16:56
 */
public class JsonEditorNotifier {

    private static final NotificationGroup NOTIFICATION_GROUP;

    static {
        NOTIFICATION_GROUP = new NotificationGroup("JsonEditorNotificationGroup", NotificationDisplayType.BALLOON, false);
    }

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
        Notification notification = NOTIFICATION_GROUP.createNotification("JsonEditor", content, notificationType, null);
        notification.notify(null);
    }

    public static void hintNotify(Editor editor, String message, long position, Runnable notifier) {
        if (message != null && !"".equals(message.trim())) {
            if (position <= (long) editor.getDocument().getTextLength() && position >= 0L) {
                editor.getCaretModel().moveToOffset((int) position);
            }
            ScrollingModel scrollingModel = editor.getScrollingModel();
            scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE);
            scrollingModel.runActionOnScrollingFinished(notifier);
        }
    }

    public static void hintInfo(Editor editor, String message) {
        hintNotify(editor, message, -1L, () -> {
            HintManager.getInstance().showInformationHint(editor, message);
        });
    }

    public static void hintError(Editor editor, String message) {
        hintNotify(editor, message, -1, () -> {
            HintManager.getInstance().showErrorHint(editor, message);
        });
    }
}
