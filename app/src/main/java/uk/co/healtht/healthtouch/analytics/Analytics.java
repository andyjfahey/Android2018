package uk.co.healtht.healthtouch.analytics;

import org.json.JSONObject;

import java.util.List;

// TODO: Don't forget to log the events on crashlytics
public class Analytics {
    public void track(String evtName, List<String> list) {
        String[] keyValue = new String[list.size()];
        track(evtName, list.toArray(keyValue));
    }

    public void track(String evtName, String... keyValue) {
        try {
            JSONObject props = new JSONObject();
            // Note: keyValue size needs to be even... we log a crash if not.
            for (int i = 0; i < keyValue.length; i += 2) {
                props.put(keyValue[i], keyValue[i + 1]);
            }
            // TODO: mixpanelTrack(evtName, props);
        }
        catch (Throwable e) {
            Crash.logException(e);
        }
    }

    public void flush() {
    }
}
