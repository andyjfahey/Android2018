package uk.co.healtht.healthtouch.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.settings.SettingsUser;


public class SecurityFragment extends BaseFragment {

    CompoundButton.OnCheckedChangeListener loginStateListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SettingsUser settingsUser = HTApplication.getInstance().getApiProvider().getSettingsUser();
            SharedPreferences sharedPreferences = HTApplication.getInstance().getSharedPreferences(
                    settingsUser.getEmail() + "prefs", Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("stayLogged", isChecked).apply();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_security, container, false);
        setupFromPreferences(v);

        setTitle(R.string.menu_security, R.color.rifle_green);

        return v;
    }

    @Override
    public void reload() {
    }

    private void setupFromPreferences(final View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SettingsUser settingsUser = HTApplication.getInstance().getApiProvider().getSettingsUser();
                SharedPreferences sharedPreferences = HTApplication.getInstance().getSharedPreferences(
                        settingsUser.getEmail() + "prefs", Context.MODE_PRIVATE);
                boolean stayLogged = sharedPreferences.getBoolean("stayLogged", true);
                setupLoginCheckBoxes(view, stayLogged);
            }
        }).start();
    }

    private void setupLoginCheckBoxes(final View view, final boolean stayLogged) {
        view.post(new Runnable() {
            @Override
            public void run() {
                CheckBox loginStateBox = (CheckBox) view.findViewById(R.id.security_login_state);
                loginStateBox.setOnCheckedChangeListener(loginStateListener);
                loginStateBox.setChecked(stayLogged);
            }
        });
    }
}
