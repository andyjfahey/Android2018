package uk.co.healtht.healthtouch.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;

import java.util.ArrayList;

public class TrackerValueSelectionDialog extends DialogFragment implements AdapterView.OnItemClickListener {

    public static final int REQUEST_SIMPLE_TRACKER = 1;
    public static final int REQUEST_SYSTOLIC_BLOOD_PRESSURE = 2;
    public static final int REQUEST_DIASTOLIC_BLOOD_PRESSURE = 3;
    public static final int REQUEST_FLUID_IN = 4;
    public static final int REQUEST_FLUID_OUT = 5;
    // for gastrointestinal
    public static final int REQUEST_GASTRO_NAUSEA = 6;
    public static final int REQUEST_GASTRO_BLOATING = 7;
    public static final int REQUEST_GASTRO_VOMITING = 8;
    public static final int REQUEST_GASTRO_DIARRHOEA = 9;
    public static final int REQUEST_GASTRO_CONSTIPATION = 10;


    private ListView trackerValuesListView;
    private ArrayList<String> valuesList;
    //extra int id for saving specific dialog last value
    private int purpose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.tracker_value_selection_dialog, null);

        if (getArguments() != null) {
            valuesList = getArguments().getStringArrayList("tracker_values_list");
        }
        trackerValuesListView = (ListView) v.findViewById(R.id.values_list);
        trackerValuesListView.setOnItemClickListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.tracker_value_selection_item, valuesList);
        adapter.setDropDownViewResource(R.layout.tracker_value_selection_item);
        trackerValuesListView.setAdapter(adapter);

        trackerValuesListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                HTApplication application = HTApplication.getInstance();
                String userIdentity = HTApplication.getInstance().getApiProvider().getSettingsUser().getEmail();
                SharedPreferences preferences = application.getSharedPreferences(application.getPackageName() + "dialogValues", Context.MODE_PRIVATE);
                int newPosition = preferences.getInt(userIdentity + "position" + purpose, valuesList.size() / 2);
                trackerValuesListView.setSelection(newPosition);
            }
        }, 100l);

        Animation animation = new AlphaAnimation(0, 1);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(500l);
        v.startAnimation(animation);
        return v;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        HTApplication application = HTApplication.getInstance();
        String userIdentity = application.getApiProvider().getSettingsUser().getEmail();
        SharedPreferences preferences = application.getSharedPreferences(application.getPackageName() + "dialogValues", Context.MODE_PRIVATE);
        preferences.edit().putInt(userIdentity + "position" + purpose, pos).apply();

        String selectedFromList = (String) (trackerValuesListView.getItemAtPosition(pos));
        Intent intent = new Intent();
        intent.putExtra("selectedValue", selectedFromList);
        dismiss();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

    public void setPurpose(int purpose) {
        this.purpose = purpose;
    }
}
