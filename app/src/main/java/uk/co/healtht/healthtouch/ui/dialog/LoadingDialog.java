package uk.co.healtht.healthtouch.ui.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.view.WindowManager;

import uk.co.healtht.healthtouch.BuildConfig;
import uk.co.healtht.healthtouch.R;

/**
 * This class shows and hides a "Loading progress dialog". There is a delay (250ms) during witch the dialog is not actually shown.
 */
public class LoadingDialog implements Runnable, OnCancelListener {

    private final int LOADING_DELAY = 250;

    private ProgressDialog dialog;
    private final Activity parentActivity;
    private OnCancelListener onCancelListener;
    private Handler handler;

    private boolean indeterminate = true;

    enum Action {
        Setup, Show, Hide
    }

    private Action action;
    private String loadingMessage;
    private boolean wasCanceled;

    public LoadingDialog(Activity parentActivity) {
        this.parentActivity = parentActivity;
        this.handler = new Handler();
    }

    public void show(boolean indeterminate) {
        loadingMessage = parentActivity.getString(R.string.progress_loading);
        show(null, loadingMessage, indeterminate);
    }

    public void show(OnCancelListener onCancelListener, String loadingMessage, boolean indeterminate) {
        this.onCancelListener = onCancelListener;
        this.loadingMessage = loadingMessage;
        this.indeterminate = indeterminate;
        runAction(Action.Setup, 0);
    }

    /**
     * hide or kill the dialog
     *
     * @param kill true when you want the dialog to DIE now, false if you want to wait a little while...
     */
    public void hide(boolean kill) {
        handler.removeCallbacks(this);
        if (kill) {
            action = Action.Hide;
            run();
        }
        else {
            runAction(Action.Hide, LOADING_DELAY);
        }
    }

    public void setIndeteminate(boolean value) {
        indeterminate = value;
    }

    public void setProgress(int percentage) {
        if (dialog != null) {
            dialog.setProgress(percentage);
        }
    }

    @Override
    public void run() {
        // NOTE: Because this runs with a delay, by the time we run, it is
        // possible that we no longer have a valid activity. If this happens,
        // we may get a "BadTokenException: Unable to add window"
        // see http://code.google.com/p/android/issues/detail?id=3953
        // We just ignore this, by wrapping on a try/catch

        try {
            if (action == Action.Setup) {
                setWindowNotTouchable(true);
                runAction(Action.Show, LOADING_DELAY);
            }
            else if (action == Action.Show) {
                if (dialog == null) {
                    dialog = new ProgressDialog(parentActivity);
                    dialog.setCancelable(true);
                    dialog.setOnCancelListener(this);
                    dialog.setMax(100);
                }
                dialog.setIndeterminate(indeterminate);
                dialog.setProgressStyle(indeterminate ? ProgressDialog.STYLE_SPINNER : ProgressDialog.STYLE_HORIZONTAL);

                dialog.setMessage(loadingMessage);
                dialog.show();
            }
            else {
                setWindowNotTouchable(false);
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;

                    // Notify the calling activity.
                    try {
                        if (wasCanceled && onCancelListener != null) {
                            onCancelListener.onCancel(null);
                        }
                    }
                    catch (Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    onCancelListener = null;
                }
            }
        }
        catch (Throwable e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public boolean isShowing() {
        return (dialog != null && dialog.isShowing());
    }

    /**
     * Called if the user dismisses the Progress Bar. Notify the calling activity.
     */
    @Override
    public void onCancel(DialogInterface dialoginterface) {
        wasCanceled = true;
        runAction(Action.Hide, 0);
    }

    private void runAction(Action action, int delay) {
        this.action = action;
        // TODO: Could posting "this" whilst having a reference to the parent activity cause a problem
        handler.postDelayed(this, delay);
    }

    private void setWindowNotTouchable(boolean isNotTouchable) {
        try {
            int mask = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            int flag = isNotTouchable ? mask : 0;
            parentActivity.getWindow().setFlags(flag, mask);
        }
        catch (Throwable e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }
}
