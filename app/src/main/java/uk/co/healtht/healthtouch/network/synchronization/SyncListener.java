package uk.co.healtht.healthtouch.network.synchronization;

import uk.co.healtht.healthtouch.api.ApiProviderListener;
import uk.co.healtht.healthtouch.api.EndPointProvider;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Julius Skripkauskas.
 */
public abstract class SyncListener implements ApiProviderListener {
    private static final int CORES = Runtime.getRuntime().availableProcessors();
    private static final TimeUnit ALIVE_UNIT = TimeUnit.SECONDS;
    private static final int ALIVE_TIME = 60;
    private static final ThreadPoolExecutor threadPoolExecutor =
            new ThreadPoolExecutor(CORES, CORES, ALIVE_TIME, ALIVE_UNIT, new LinkedBlockingQueue<Runnable>());

    @Override
    public void onDataLoaded(final EndPointProvider provider, final Object providerData) {
        Thread dx = new Thread(new Runnable() {
            @Override
            public void run() {
                dataLoaded(provider, providerData);
            }
        });
        dx.setPriority(Thread.MIN_PRIORITY);
        threadPoolExecutor.execute(dx);
    }

    @Override
    public void onDataLoadedError(final EndPointProvider provider, final Map<String, Object> errorObj, final String debugMsg, final int errorCode) {
        Thread dx = new Thread(new Runnable() {
            @Override
            public void run() {
                dataLoadError(provider, errorObj, debugMsg, errorCode);
            }
        });
        dx.setPriority(Thread.MIN_PRIORITY);
        threadPoolExecutor.execute(dx);
    }

    @Override
    public void onNoData(EndPointProvider endPointProvider){

    }

    public abstract void dataLoaded(EndPointProvider provider, Object providerData);

    public abstract void dataLoadError(EndPointProvider provider, Map<String, Object> errorObj, String debugMsg, int errorCode);
}
