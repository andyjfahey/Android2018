package uk.co.healtht.healthtouch.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andexert.library.RippleView;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.analytics.Crash;

/**
 * Created by Julius Skripkauskas.
 */
public class SettingsSecurityPrivacyFragment extends BaseFragment implements RippleView.OnRippleCompleteListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_security_privacy, container, false);
        RippleView settings_security = (RippleView) v.findViewById(R.id.settings_security);
        settings_security.setOnRippleCompleteListener(this);
        RippleView settings_privacy = (RippleView) v.findViewById(R.id.settings_privacy);
        settings_privacy.setOnRippleCompleteListener(this);

        setTitle(R.string.security_and_privacy, R.color.rifle_green);

        return v;
    }

    @Override
    public void reload() {
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.settings_security:
                startFragment(SecurityFragment.class, null);
                break;
            case R.id.settings_privacy:
                startFragment(PrivacyFragment.class, null);
                break;
            default:
                Crash.reportCrash("Unknown click id");
        }
    }
}
