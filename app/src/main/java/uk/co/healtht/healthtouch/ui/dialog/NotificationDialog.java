package uk.co.healtht.healthtouch.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.text.Html;
import android.text.TextUtils;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.analytics.Crash;

import java.util.LinkedList;
import java.util.Queue;

public class NotificationDialog {

    private static class DialogData {

        String title;
        String text;
        String actionText;

        DialogData(String title, String text, String actionText) {
            this.title = title;
            this.text = text;
            this.actionText = actionText;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((actionText == null) ? 0 : actionText.hashCode());
            result = prime * result + ((text == null) ? 0 : text.hashCode());
            result = prime * result + ((title == null) ? 0 : title.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof DialogData) {
                DialogData other = (DialogData) obj;
                return TextUtils.equals(title, other.title) //
                        && TextUtils.equals(text, other.text) //
                        && TextUtils.equals(actionText, other.actionText);
            }

            return false;
        }
    }

    private Queue<DialogData> notificationQueue = new LinkedList<>();
    private Activity activity;

    public NotificationDialog(Activity activity) {
        this.activity = activity;
    }

    public void showNotification(String title, String text, String actionText) {

        DialogData data = new DialogData(title, text, actionText);
        if (!notificationQueue.contains(data)) {
            notificationQueue.add(data);

            if (notificationQueue.size() == 1) {
                // This is the first element on the queue, kick the dialog
                showAlert(data);
            }
        }
    }

    private void showAlert(DialogData data) {
        if (data != null) {
            showAlert(data.title, data.text, data.actionText);
        }
    }

    private void showNextNofitication() {
        // remove the current notification
        notificationQueue.poll();

        // We keep the data on the queue, so it will not be possible to show the same message as the one being displayed.
        DialogData data = notificationQueue.peek();
        showAlert(data);
    }

    private void showAlert(String title, String text, String alertButtonText) {
        if (alertButtonText == null) {
            alertButtonText = activity.getString(R.string.btn_OK);
        }

        AlertDialog dialog = new AlertDialog.Builder(activity).setTitle(title).setPositiveButton(alertButtonText, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.dismiss();
                }
                catch (Throwable e) {
                    Crash.logException(e);
                }
            }
        }).setMessage(Html.fromHtml(text)).create();

        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                showNextNofitication();
            }
        });

        dialog.show();
    }
}
