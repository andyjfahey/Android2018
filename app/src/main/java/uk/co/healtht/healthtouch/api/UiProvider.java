package uk.co.healtht.healthtouch.api;

import android.support.v4.util.SimpleArrayMap;

import java.util.LinkedHashSet;
import java.util.Set;

public class UiProvider {

    public interface UiProviderListener {
        void onUiChange(UiEvent event, Object providerData);
    }

    public enum UiEvent {
        LOGIN, //
        LOGOUT, //
    }

    private SimpleArrayMap<UiEvent, Set<UiProviderListener>> listeners = new SimpleArrayMap<>();

    public void addListener(UiEvent event, UiProviderListener listener) {
        Set<UiProviderListener> eventListeners = listeners.get(event);
        if (eventListeners == null) {
            eventListeners = new LinkedHashSet<>();
            listeners.put(event, eventListeners);
        }
        eventListeners.add(listener);
    }

    public void removeListener(UiEvent event, UiProviderListener listener) {
        Set<UiProviderListener> eventListeners = listeners.get(event);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    public void broadcastEvent(UiEvent event, Object providerData) {
        Set<UiProviderListener> eventListeners = listeners.get(event);
        if (eventListeners != null) {
            // Make a copy, to avoid concurrent modification, if listeners unregister while notified
            UiProviderListener[] listenersCopy = eventListeners.toArray(new UiProviderListener[eventListeners.size()]);
            for (UiProviderListener listener : listenersCopy) {
                listener.onUiChange(event, providerData);
            }
        }
    }

}
