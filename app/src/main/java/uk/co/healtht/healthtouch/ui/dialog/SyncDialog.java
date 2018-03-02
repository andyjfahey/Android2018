package uk.co.healtht.healthtouch.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;

public class SyncDialog extends AlertDialog.Builder {
    //--------------------VARIABLES--------------------
    protected Activity activity;
    protected View dialogLayout;
    protected TextView message;
    protected ProgressBar progressBar;
    protected AlertDialog dialog;
    protected Object tag;

    //--------------------MAIN-METHODS-----------------

    public SyncDialog(Activity activity) {
        super(activity);
        this.activity = activity;

        buildDialog();
        initDialogViews();
    }

    @Override
    public AlertDialog create() {
        dialog = super.create();
        return dialog;
    }

    /**
     * Dismiss dialog.
     */
    public void dismiss() {
        dialog.dismiss();
    }

    /**
     * Builds/inflates custom layout view for this dialog.
     */
    protected void buildDialog() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        dialogLayout = inflater.inflate(R.layout.sync_dialog, new LinearLayout(activity), false);
        setView(dialogLayout);
        setCancelable(false);
    }

    /**
     * Sets view references.
     */
    protected void initDialogViews() {
        message = (TextView) dialogLayout.findViewById(R.id.msg);
        progressBar = (ProgressBar) dialogLayout.findViewById(R.id.progressBar);
    }

    public void setDialogMessage(String msg) {
        if (message != null && msg != null) {
            message.setText(msg);
        }
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public void hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
}