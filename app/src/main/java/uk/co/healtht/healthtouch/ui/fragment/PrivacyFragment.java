package uk.co.healtht.healthtouch.ui.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.settings.SettingsApplication;


public class PrivacyFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_privacy, container, false);

        SettingsApplication appSettings = apiProvider.getSettingsApplication();
        setupViewLink(v, appSettings.getPrivacyLink(), R.id.privacy_link);
        setupViewLink(v, appSettings.getSupportEmail(), R.id.privacy_email);

        setTitle(R.string.title_privacy, R.color.rifle_green);

        return v;
    }

    @Override
    public void reload() {
    }

    private void setupViewLink(View parent, final String url, int viewId) {
        TextView linkView = (TextView) parent.findViewById(viewId);
        linkView.setText(url.replace("http://", "").replace("mailto:", ""));
        linkView.setPaintFlags(linkView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        linkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(url);
            }
        });
    }
}
