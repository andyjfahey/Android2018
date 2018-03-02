package uk.co.healtht.healthtouch.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;

/**
 * Created by Julius Skripkauskas.
 */
public class ActionDialog extends AlertDialog.Builder {
    //--------------------VARIABLES--------------------
    protected Activity activity;
    protected View dialogLayout;
    protected TextView message;
    protected Button okBtn, cancelBtn;
    protected AlertDialog dialog;
    protected Object tag;

    //--------------------MAIN-METHODS-----------------

    public ActionDialog(Activity activity) {
        super(activity);
        this.activity = activity;

        buildDialog();
        initDialogViews();
    }

    View.OnClickListener cancelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

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
        dialogLayout = inflater.inflate(R.layout.action_dialog, new LinearLayout(activity), false);
        setView(dialogLayout);
        setCancelable(false);
    }

    /**
     * Sets view references.
     */
    protected void initDialogViews() {
        message = (TextView) dialogLayout.findViewById(R.id.msg);
        okBtn = (Button) dialogLayout.findViewById(R.id.action_dialog_ok);
        cancelBtn = (Button) dialogLayout.findViewById(R.id.action_dialog_cancel);
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

    public void setOkButton(String text, View.OnClickListener listener){
        okBtn.setVisibility(View.VISIBLE);
        okBtn.setText(text);
        okBtn.setOnClickListener(listener);
    }

    public void setCancelButton(String text, View.OnClickListener listener){
        cancelBtn.setVisibility(View.VISIBLE);
        cancelBtn.setText(text);
        cancelBtn.setOnClickListener(listener);
    }

    public void setCancelButton(String text){
        cancelBtn.setVisibility(View.VISIBLE);
        cancelBtn.setText(text);
        cancelBtn.setOnClickListener(cancelClick);
    }
}