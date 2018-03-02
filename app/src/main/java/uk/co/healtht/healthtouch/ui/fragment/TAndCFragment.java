package uk.co.healtht.healthtouch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.healtht.healthtouch.R;


public class TAndCFragment extends WebViewFragment {

    public static void showTandC(BaseFragment launcher) {
        show(launcher, "file:///android_asset/terms.html", R.string.title_t_and_c);
    }

    private static void show(BaseFragment launcher, String url, int titleId) {
        Bundle data = new Bundle();
        data.putString(ARG_URL, url);
        data.putInt("title_id", titleId);

        launcher.startFragment(TAndCFragment.class, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int titleId = getArguments().getInt("title_id");
        setTitle(titleId);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

}
