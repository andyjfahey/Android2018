package uk.co.healtht.healthtouch.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.comms.DividerItemDecoration;
import uk.co.healtht.healthtouch.model.delegate.AbstractReminderDelegate;
import uk.co.healtht.healthtouch.model.entities.HTTrackerReminderAbstract;
import uk.co.healtht.healthtouch.ui.adapters.GeneralReminderListAdapter;
import uk.co.healtht.healthtouch.ui.widget.OnListItemClick;
import uk.co.healtht.healthtouch.util_helpers.NewAlarmUtil;
import uk.co.healtht.healthtouch.utils.ViewUtils;

/**
 * Created by andyj on 16/01/2018.
 */

public abstract class AbstractReminderListFragment<T extends HTTrackerReminderAbstract> extends BaseFragment implements
        View.OnClickListener
{
    private RecyclerView reminderList;
    private FloatingActionButton reminderAdd;

    private List<T> remindersList;
    private GeneralReminderListAdapter<T> reminderListAdapter;

    private final int requestCode = 33;

    private int reminder_fk_id;
    private String reminderName;

    protected abstract List<T> getRemindersList(int fk_id);
    protected abstract GeneralReminderListAdapter<T> getReminderListAdapter(List<T>  remindersList);
    protected abstract AbstractReminderDelegate<T> getDBDelegate();
    protected abstract T getNewReminder();

    protected abstract int getFragmentTitleColourId();
    protected abstract String getFragmentTitle();
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try {
            // tracker = (HTTrackerSync) getArguments().getSerializable("tracker");
            reminder_fk_id = (Integer) getArguments().getSerializable("reminder_fk_id");
            reminderName = (String) getArguments().getSerializable("reminderName");
        } catch (Exception e) {
            //
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_settings_reminders_copy, container, false);

        reminderList = (RecyclerView) v.findViewById(R.id.reminder_list);
        reminderAdd = (FloatingActionButton) v.findViewById(R.id.reminder_add);

        setTitle(getFragmentTitle(), getFragmentTitleColourId());
        ViewUtils.setText(v, R.id.tracker_label, reminderName);

        reminderAdd.setOnClickListener(this);

        getTrackersReminderListFromDB();
        return v;
    }

    private void getTrackersReminderListFromDB()
    {
        remindersList = getRemindersList(reminder_fk_id); // new D().getAllByTrackerId(tracker.tracker_id);
        reminderListAdapter = getReminderListAdapter(remindersList); //new ReminderListAdapter(remindersList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        reminderList.setLayoutManager(mLayoutManager);
        reminderList.setItemAnimator(new DefaultItemAnimator());
        reminderList.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        reminderListAdapter.setOnDeleteListener(deleteListener);
        reminderListAdapter.setOnItemListener(itemListener);
        reminderList.setAdapter(reminderListAdapter);
    }

    @Override
    public void reload()
    {
    }

    OnListItemClick<T> deleteListener = new OnListItemClick<T>()
    {
        @Override
        public void onItemListClicked(final int position, View view, final T itemData)
        {

            AlertDialog dialog = new AlertDialog.Builder(reminderList.getContext())
                    .setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            deleteTrackEntry(position, itemData);
/*                            reminderListAdapter.deletePosition(position);

                            itemData.synced = false;
                            itemData.deleted_at = new Date(System.currentTimeMillis());
                            // re-synced with web
                            getDBDelegate().update(itemData);*/

                            //remove associated alarm
                            NewAlarmUtil.removeAlarm(getActivity(), itemData);
                        }
                    })
                    .setNegativeButton(R.string.btn_cancel, null)
                    .setMessage(R.string.tracker_delete_confirmation)
                    .create();
            dialog.show();
        }
    };

    private void gotoAddReminderFragment( final T itemData, String fragmentTitle) {
        Bundle data = new Bundle();
        data.putSerializable("reminder_fk_id", reminder_fk_id);
        data.putSerializable("reminderName", reminderName);
        data.putSerializable("reminder", itemData);
        data.putSerializable("fragmentTitle", fragmentTitle);

        startEditReminderFragment(data, requestCode);
    }

    OnListItemClick<T> itemListener = new OnListItemClick<T>()
    {
        @Override
        public void onItemListClicked(final int position, View view, final T itemData)
        {
            gotoAddReminderFragment(itemData, "Edit Reminders");
        }
    };

    private void deleteTrackEntry(int position, T reminder)
    {
        reminderListAdapter.deletePosition(position);

        reminder.synced = false;
        reminder.deleted_at = new Date(System.currentTimeMillis());
        // re-synced with web
        getDBDelegate().update(reminder);

        //remove associated alarm
        NewAlarmUtil.removeAlarm(getActivity(), reminder);
    }

    protected abstract void startEditReminderFragment(Bundle data, int requestCode );

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.reminder_add)
        {
            T newReminder = getNewReminder();
            newReminder.setFK_Id(reminder_fk_id);
            newReminder.created_at = new Date(System.currentTimeMillis());
            newReminder.server_id = null;
            newReminder.entity = newReminder.getClass().getSimpleName();
            gotoAddReminderFragment(newReminder, "Add Reminder");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK && requestCode == this.requestCode)
        {
            getTrackersReminderListFromDB();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
