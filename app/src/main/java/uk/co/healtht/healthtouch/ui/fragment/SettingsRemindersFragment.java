package uk.co.healtht.healthtouch.ui.fragment;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import uk.co.healtht.healthtouch.HTApplication;
import uk.co.healtht.healthtouch.R;
import uk.co.healtht.healthtouch.analytics.Crash;
import uk.co.healtht.healthtouch.api.ApiCache;
import uk.co.healtht.healthtouch.api.EndPointProvider;
import uk.co.healtht.healthtouch.network.LocalNotificationService;
import uk.co.healtht.healthtouch.network.synchronization.SyncDb;
import uk.co.healtht.healthtouch.proto.Monitor;
import uk.co.healtht.healthtouch.proto.Tracker;
import uk.co.healtht.healthtouch.proto.TrackerReply;
import uk.co.healtht.healthtouch.utils.Cron;
import uk.co.healtht.healthtouch.utils.TextUtils;
import uk.co.healtht.healthtouch.utils.ViewUtils;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SettingsRemindersFragment extends BaseFragment {
    private Tracker tracker;
    private ArrayList<Cron> cronList = new ArrayList<>();
    private List<Monitor> monitors;
    private EndPointProvider monitorProvider;

    static AdapterView.OnItemSelectedListener recSpinnerItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Cron cron = (Cron) parent.getTag();
            View panelWeekDay = (View) parent.getTag(R.id.panel_day_of_week);
            View panelMonthDay = (View) parent.getTag(R.id.panel_day_of_month);
            View panelTime = (View) parent.getTag(R.id.panel_time);

            switch (position) {
                case 0: // Off
                    cron.recurrence = Cron.Recurrence.NONE;
                    panelWeekDay.setVisibility(View.GONE);
                    panelMonthDay.setVisibility(View.GONE);
                    panelTime.setVisibility(View.GONE);
                    break;

                case 1: // Daily
                    cron.recurrence = Cron.Recurrence.DAILY;
                    panelWeekDay.setVisibility(View.GONE);
                    panelMonthDay.setVisibility(View.GONE);
                    panelTime.setVisibility(View.VISIBLE);
                    break;

                case 2: // Weekly
                    cron.recurrence = Cron.Recurrence.WEEKLY;
                    panelWeekDay.setVisibility(View.VISIBLE);
                    panelMonthDay.setVisibility(View.GONE);
                    panelTime.setVisibility(View.VISIBLE);
                    break;

                case 3: // Monthly
                    cron.recurrence = Cron.Recurrence.MONTLY;
                    panelWeekDay.setVisibility(View.GONE);
                    panelMonthDay.setVisibility(View.VISIBLE);
                    panelTime.setVisibility(View.VISIBLE);
                    break;

                default:
                    Crash.reportCrash("onItemSelected: " + position);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    static AdapterView.OnItemSelectedListener dayOfWeekItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((Cron) parent.getTag()).setWeekDay(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    static AdapterView.OnItemSelectedListener dayOfMonthItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((Cron) parent.getTag()).setDay(position + 1);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    static View.OnClickListener timeTextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Cron cron = (Cron) v.getTag();
            final TextView timeText = (TextView) v;

            TimePickerDialog fromDatePickerDialog = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    cron.hourOfDay = hourOfDay;
                    cron.minute = minute;

                    timeText.setText(cron.getFormatedTime());
                }
            }, cron.hourOfDay, cron.minute, false);

            fromDatePickerDialog.show();
        }
    };

    View.OnClickListener saveReminderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: Need a bulk data provider instead of loop
            for (int i = 0; i < cronList.size(); i++) {
                Cron cron = cronList.get(i);
                Monitor monitor = (i < monitors.size()) ? monitors.get(i) : null;
                EndPointProvider endPointProvider;

                if (cron.recurrence == Cron.Recurrence.NONE) {
                    endPointProvider = deleteMonitor(monitor);
                } else {
                    endPointProvider = createUpdateMonitor(cron, monitor);
                }

                // TODO: Fix when we have a bulk provider
                if (monitorProvider == null && endPointProvider != null) {
                    monitorProvider = endPointProvider;

                    loadingDialog.show(true);
                    monitorProvider.addListener(SettingsRemindersFragment.this);
                }
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();

        // TODO: Need to register + add spinner onStart()
        if (monitorProvider != null) {
            monitorProvider.removeListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        tracker = (Tracker) getArguments().getSerializable("tracker");
        if (tracker != null) {
            monitors = tracker.monitors;
        }
        List<String> tempCronList = new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment_settings_reminders, container, false);
        ViewGroup panelReminderList = (ViewGroup) v.findViewById(R.id.panel_reminder_list);

        boolean hasFreeEntries = false;
        for (Monitor monitor : monitors) {
            // If we have free entries, if we have at least one monitor that is not enabled
            hasFreeEntries |= !monitor.enabled;

            if (!tempCronList.contains(monitor.cron)) {
                tempCronList.add(monitor.cron);
                cronList.add(addReminderView(inflater, panelReminderList, monitor));
            }
        }

        // If we have no extras or less than two, add one extra
        while (!hasFreeEntries || cronList.size() < 2) {
            cronList.add(addReminderView(inflater, panelReminderList, null));
            hasFreeEntries = true;
        }

        v.findViewById(R.id.btn_save).setOnClickListener(saveReminderListener);
        setTitle(R.string.home_settings, R.color.rifle_green);
        return v;
    }

    private EndPointProvider createUpdateMonitor(Cron cron, Monitor monitor) {
        String cronStr = cron.toString();
        EndPointProvider endPointProvider = null;

        // if cron didn't change, no need to update server
        if (monitor == null || !cronStr.equals(monitor.cron)) {
            HashMap<String, String> msg = new HashMap<>();
            msg.put("cron", cronStr);

            if (monitor == null) {
                endPointProvider = apiProvider.getCreateMonitor(tracker.uri);
                stashCreateAction(msg, endPointProvider);
                createReminderNotification(msg.get("cron"), tracker, getActivity());
            } else {
                updateReminderNotification(msg, monitor);
                endPointProvider = apiProvider.getUpdateMonitor(monitor.uri);
                stashUpdateAction(msg, endPointProvider, monitor);
            }
        }
        return endPointProvider;
    }

    private EndPointProvider deleteMonitor(Monitor monitor) {
        EndPointProvider endPointProvider = null;
        if (monitor != null) {
            deleteReminderNotification(monitor);
            endPointProvider = apiProvider.getDeleteMonitor(monitor.uri);
            stashDeleteAction(endPointProvider, monitor);
        }
        return endPointProvider;
    }

    //first remove reminder if it already exists then create new alarm with new cron
    private void updateReminderNotification(HashMap<String, String> msg, Monitor monitor) {
        Activity activity = getActivity();

        if (activity != null) {
            String hash = tracker.uri + monitor.cron;
            removeReminderInPreferences(activity, hash);
            LocalNotificationService.removeAlarm(activity, hash.hashCode());
            createReminderNotification(msg.get("cron"), tracker, activity);
        }
    }

    public static synchronized void removeReminderInPreferences(Context context, String hash) {
        SharedPreferences preferences = context.getSharedPreferences("reminders", Context.MODE_PRIVATE);
        Set<String> hashSet = preferences.getStringSet("hash", new HashSet<String>());
        hashSet.remove(hash);
        preferences.edit().putStringSet("hash", hashSet).apply();
    }

    //delete reminder manually, invoked by user
    private void deleteReminderNotification(Monitor monitor) {
        Activity activity = getActivity();

        if (activity != null) {
            String hash = tracker.uri + monitor.cron;
            removeReminderInPreferences(activity, hash);
            LocalNotificationService.removeAlarm(activity, hash.hashCode());
        }
    }

    public static void createReminderNotification(String cron, Tracker tracker, Context context) {
        if (context != null) {
            Intent notificationIntent = new Intent(context, LocalNotificationService.class);
            notificationIntent.putExtra("tracker", tracker);
            notificationIntent.putExtra("cron", cron);
            SharedPreferences preferences = context.getSharedPreferences("reminders", Context.MODE_PRIVATE);
            String hash = tracker.uri + cron;
            Set<String> hashSet = preferences.getStringSet("hash", new HashSet<String>());
            hashSet.add(hash);

            notificationIntent.setAction(hash.hashCode() + "");
            saveHashPref(preferences, hashSet);
            LocalNotificationService.setupAlarm(context, notificationIntent, hash.hashCode(), new Cron(cron, false));
        }
    }

    protected static void saveHashPref(final SharedPreferences preferences, final Set<String> hashSet) {
        final Handler handler = HTApplication.getInstance().getUiHandler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    preferences.edit().putStringSet("hash", hashSet).apply();
                } catch (ConcurrentModificationException ex) {
                    handler.postDelayed(this, 400);
                }
            }
        });
    }

    private static String getDaySuffix(int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    private Cron addReminderView(LayoutInflater inflater, ViewGroup panel, Monitor monitor) {
        final Cron cron = (monitor == null) ? new Cron() : new Cron(monitor.cron, monitor.enabled);

        Context ctx = panel.getContext();
        View itemView = inflater.inflate(R.layout.reminder_item, panel, false);

        setupReminderViewRecSpinner(itemView, cron);
        setupReminderViewWeekDaySpinner(itemView, cron, ctx);
        setupReminderViewMonthDaySpinner(itemView, cron, ctx);
        panel.addView(itemView);

        View separator = new View(ctx);
        separator.setBackgroundColor(getResources().getColor(R.color.menu_separator));
        panel.addView(separator, ViewGroup.LayoutParams.MATCH_PARENT, ViewUtils.dipToPixels(ctx, 1));

        return cron;
    }

    private void setupReminderViewRecSpinner(View itemView, Cron cron) {
        final View panelWeekDay = itemView.findViewById(R.id.panel_day_of_week);
        final View panelMonthDay = itemView.findViewById(R.id.panel_day_of_month);
        final View panelTime = itemView.findViewById(R.id.panel_time);

        // Spinner Recurrence
        Spinner recSpinner = (Spinner) itemView.findViewById(R.id.field_recurrence);
        recSpinner.setOnItemSelectedListener(recSpinnerItemListener);
        recSpinner.setTag(R.id.panel_day_of_week, panelWeekDay);
        recSpinner.setTag(R.id.panel_day_of_month, panelMonthDay);
        recSpinner.setTag(R.id.panel_time, panelTime);
        recSpinner.setTag(cron);
        recSpinner.setSelection(cron.recurrence.ordinal());
    }

    private void setupReminderViewWeekDaySpinner(View itemView, Cron cron, Context ctx) {
        // Spinner Day of Week
        ArrayList<String> weekDays = new ArrayList<>(7);
        for (String weekDay : new DateFormatSymbols(Locale.UK).getWeekdays()) {
            // WTF - Is returning element zero as ""
            if (!TextUtils.isEmpty(weekDay)) {
                weekDays.add(weekDay);
            }
        }
        ArrayAdapter<String> weekDaysAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, weekDays);
        Spinner dayOfWeekSpinner = (Spinner) itemView.findViewById(R.id.field_day_of_week);
        dayOfWeekSpinner.setAdapter(weekDaysAdapter);
        dayOfWeekSpinner.setSelection(cron.getWeekDay());
        dayOfWeekSpinner.setTag(cron);
        dayOfWeekSpinner.setOnItemSelectedListener(dayOfWeekItemListener);
    }

    private void setupReminderViewMonthDaySpinner(View itemView, Cron cron, Context ctx) {
        // Spinner Day of Month
        ArrayList<String> monthDays = new ArrayList<>(31);
        for (int n = 1; n <= 28; n++) {
            monthDays.add(n + getDaySuffix(n));
        }

        ArrayAdapter<String> monthDaysAdapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, monthDays);
        Spinner dayOfMonthSpinner = (Spinner) itemView.findViewById(R.id.field_day_of_month);
        dayOfMonthSpinner.setAdapter(monthDaysAdapter);
        dayOfMonthSpinner.setSelection(cron.getDay() - 1);
        dayOfMonthSpinner.setTag(cron);
        dayOfMonthSpinner.setOnItemSelectedListener(dayOfMonthItemListener);

        final TextView timeText = (TextView) itemView.findViewById(R.id.field_time);
        timeText.setText(cron.getFormatedTime());
        timeText.setTag(cron);
        timeText.setOnClickListener(timeTextListener);
    }

    private void stashUpdateAction(final HashMap<String, String> data, final EndPointProvider provider, final Monitor monitor) {
        Thread dx = new Thread(new Runnable() {
            @Override
            public void run() {
                SyncDb syncDb = HTApplication.getInstance().getSyncDb();
                EndPointProvider endPointProvider = apiProvider.getTrackers();
                endPointProvider = syncDb.getProvider(endPointProvider.getEndPoint().getUri());

                if (endPointProvider != null) {
                    Tracker trackerStashed = removeMonitor(endPointProvider, monitor);
                    monitor.cron = data.get("cron");
                    trackerStashed.monitors.add(monitor);
                    //sync
                    syncDb.saveProvider(endPointProvider.getEndPoint().getUri(), endPointProvider);
                    apiProvider.getProviderCache().put(endPointProvider.getEndPoint().getUri(), endPointProvider);
                }

                if (monitor.uri != null) {
                    stashMonitorRequest(data, provider);
                } else {
                    EndPointProvider deletePendingCreateProvider = apiProvider.getCreateMonitor(tracker.uri);
                    apiProvider.getApiCache().removePendingRequest(deletePendingCreateProvider);
                    endPointProvider = apiProvider.getCreateMonitor(tracker.uri);
                    stashCreateAction(data, endPointProvider);
                }

                onDataLoaded(endPointProvider, data);
            }
        });
        dx.start();
    }

    private void stashMonitorRequest(final HashMap<String, String> data, final EndPointProvider provider) {
        ApiCache apiCache = apiProvider.getApiCache();
        provider.setLastReqData(data);
        apiCache.addPendingRequest(provider);
    }

    private void stashCreateAction(final HashMap<String, String> data, final EndPointProvider provider) {
        Thread dx = new Thread(new Runnable() {
            @Override
            public void run() {
                SyncDb syncDb = HTApplication.getInstance().getSyncDb();
                EndPointProvider endPointProvider = apiProvider.getTrackers();
                endPointProvider = syncDb.getProvider(endPointProvider.getEndPoint().getUri());

                if (endPointProvider != null) {
                    TrackerReply trackerReply = (TrackerReply) endPointProvider.getProviderData();
                    Tracker trackerStashed = trackerReply.getTrackerById(tracker.uri);
                    Monitor monitor = new Monitor();
                    monitor.enabled = true;
                    monitor.cron = data.get("cron");
                    trackerStashed.monitors.add(monitor);
                    tracker.monitors.add(monitor);
                    //sync
                    syncDb.saveProvider(endPointProvider.getEndPoint().getUri(), endPointProvider);
                    apiProvider.getProviderCache().put(endPointProvider.getEndPoint().getUri(), endPointProvider);
                }

                stashMonitorRequest(data, provider);
                onDataLoaded(endPointProvider, data);
            }
        });
        dx.start();
    }

    private void stashDeleteAction(final EndPointProvider provider, final Monitor monitor) {
        Thread dx = new Thread(new Runnable() {
            @Override
            public void run() {
                SyncDb syncDb = HTApplication.getInstance().getSyncDb();
                EndPointProvider endPointProvider = apiProvider.getTrackers();
                endPointProvider = syncDb.getProvider(endPointProvider.getEndPoint().getUri());

                if (endPointProvider != null) {
                    removeMonitor(endPointProvider, monitor);
                    tracker.monitors.remove(monitor);
                    //sync
                    syncDb.saveProvider(endPointProvider.getEndPoint().getUri(), endPointProvider);
                    apiProvider.getProviderCache().put(endPointProvider.getEndPoint().getUri(), endPointProvider);
                }

                if (monitor.uri != null) {
                    stashMonitorRequest(null, provider);
                } else {
                    EndPointProvider deletePendingCreateProvider = apiProvider.getCreateMonitor(tracker.uri);
                    EndPointProvider deletePendingUpdateProvider = apiProvider.getUpdateMonitor(monitor.uri);
                    apiProvider.getApiCache().removePendingRequest(deletePendingUpdateProvider);
                    apiProvider.getApiCache().removePendingRequest(deletePendingCreateProvider);
                }
                onDataLoaded(endPointProvider, null);
            }
        });
        dx.start();
    }

    private Tracker removeMonitor(EndPointProvider provider, Monitor monitor) {
        TrackerReply trackerReply = (TrackerReply) provider.getProviderData();
        Tracker trackerStashed = trackerReply.getTrackerById(tracker.uri);
        int position = -1;

        for (Monitor element : trackerStashed.monitors) {
            if (element.uri == null || element.uri.equals(monitor.uri)) {
                position = trackerStashed.monitors.indexOf(element);
            }
        }
        if (position >= 0) {
            trackerStashed.monitors.remove(position);
        }

        return trackerStashed;
    }

    @Override
    public void onDataLoaded(EndPointProvider provider, Object providerData) {
        super.onDataLoaded(provider, providerData);

        finish(RESULT_OK);
    }

    @Override
    public void reload() {
    }
}
