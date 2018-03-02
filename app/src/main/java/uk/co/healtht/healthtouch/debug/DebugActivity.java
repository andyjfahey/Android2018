package uk.co.healtht.healthtouch.debug;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.MainActivity;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.comms.CommsProcessor;
import uk.co.healtht.healthtouch.settings.SettingsApplication;
import uk.co.healtht.healthtouch.settings.SettingsDebug;
import uk.co.healtht.healthtouch.utils.TextUtils;

public class DebugActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        setupCheckableView(R.id.cbShowFps, SettingsDebug.isShowFpsEnabled());
        setupCheckableView(R.id.cbShowDebugNotif, SettingsDebug.isShowDebugNotificationEnabled());
        setupCheckableView(R.id.cbIsFullNetworkDebug, SettingsDebug.isFullNetworkLog());
        setupCheckableView(R.id.cbIsShowErrorToasts, SettingsDebug.isShowErrorToasts());
        setupClickableView(R.id.btnSetDebugServer);
        setupClickableView(R.id.btnServerDev);
        setupClickableView(R.id.btnServerLive);

        ((TextView) findViewById(R.id.editDebugServer)).setText(SettingsDebug.getDebugServer());

        SettingsApplication appSettings = HTApplication.getInstance().getApiProvider().getSettingsApplication();
        ((TextView) findViewById(R.id.lastPushReceived)).setText("Last Push Received: " + appSettings.getLastPushUri());
    }

    private void setupClickableView(int id) {
        View view = findViewById(id);
        view.setOnClickListener(this);
    }

    private void setupCheckableView(int id, boolean checked) {
        View view = findViewById(id);
        view.setOnClickListener(this);
        ((Checkable) view).setChecked(checked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cbShowFps:
                SettingsDebug.setShowFps(((Checkable) v).isChecked(), true);
                break;

            case R.id.cbShowDebugNotif:
                SettingsDebug.setShowDebugNotification(((Checkable) v).isChecked());
                break;

            case R.id.cbIsFullNetworkDebug: {
                SettingsDebug.setFullNetworkLog(((Checkable) v).isChecked());
                break;
            }

            case R.id.cbIsShowErrorToasts: {
                SettingsDebug.setShowErrorToasts(((Checkable) v).isChecked());
                break;
            }

            case R.id.btnServerDev: {
                ((EditText) findViewById(R.id.editDebugServer)).setText(CommsProcessor.SERVER_DEV);
                setDebugServer();
                break;
            }

            case R.id.btnServerLive: {
                ((EditText) findViewById(R.id.editDebugServer)).setText(CommsProcessor.SERVER_LIVE);
                setDebugServer();
                break;
            }

            case R.id.btnSetDebugServer:
                setDebugServer();
                break;

            case R.id.lastPushReceived: {
                SettingsApplication appSettings = HTApplication.getInstance().getApiProvider().getSettingsApplication();
                showPushNofification(appSettings.getLastPushUri());
                finish();
                break;
            }

            default:
                break;
        }

    }

    @SuppressLint("NewApi")
    private void copyToClipboard(CharSequence textToCopy) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", textToCopy);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void showPushNofification(String uriString) {
        if (!uriString.startsWith("push://")) {
            uriString = "push://" + uriString;
        }
        Intent eventIntent = new Intent(this, MainActivity.class);
        eventIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        eventIntent.setData(Uri.parse(uriString));
        eventIntent.putExtra("payload", uriString);

        startActivity(eventIntent);
    }

    private void setDebugServer() {
        String debugServer = ((EditText) findViewById(R.id.editDebugServer)).getText().toString();
        if (TextUtils.isEmpty(debugServer) || debugServer.startsWith("http://") || debugServer.startsWith("https://")) {
            SettingsDebug.setDebugServer(debugServer);
            HTApplication.getInstance().getApiProvider().setServer(debugServer);
            Toast.makeText(this, "Server set!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Invalid server!!", Toast.LENGTH_LONG).show();
        }
    }
}
