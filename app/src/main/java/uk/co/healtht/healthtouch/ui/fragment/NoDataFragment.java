package uk.co.healtht.healthtouch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.healtht.healthtouch.R;

public class NoDataFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_no_data, container, false);

        Bundle data = getArguments();
        int titleTextId = data.getInt("titleText", R.string.app_name);
        int titleColorId = data.getInt("titleColor", R.color.indigo);
        titleTextId = titleTextId != 0 ? titleTextId :  R.string.app_name;
        titleColorId = titleColorId != 0 ? titleColorId : R.color.indigo;
        setTitle(titleTextId, titleColorId);

        return view;
    }

    @Override
    public void reload() {
        getActivity().recreate();
    }
}
