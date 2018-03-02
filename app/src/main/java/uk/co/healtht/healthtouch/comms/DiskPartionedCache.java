package uk.co.healtht.healthtouch.comms;

import com.android.volley.Cache;
import com.android.volley.toolbox.DiskBasedCache;

import java.io.File;

public class DiskPartionedCache implements Cache {

    private Cache mainCache, smallObjectsCache;

    public DiskPartionedCache(File mainDir, File smallDir) {
        // Total of 15MB cache
        mainCache = new DiskBasedCache(mainDir, 14 * 1024 * 1024);
        smallObjectsCache = new DiskBasedCache(smallDir, 1024 * 1024);
    }

    @Override
    public Entry get(String key) {
        Entry res = mainCache.get(key);
        return res == null ? smallObjectsCache.get(key) : res;
    }

    @Override
    public void put(String key, Entry entry) {
        // With 50K, in the worst case scenario we will keep 20 icons
        if (entry.data.length <= 50 * 1024) {
            smallObjectsCache.put(key, entry);
        }
        else {
            mainCache.put(key, entry);
        }
    }

    @Override
    public void initialize() {
        mainCache.initialize();
        smallObjectsCache.initialize();
    }

    @Override
    public void invalidate(String key, boolean fullExpire) {
        mainCache.invalidate(key, fullExpire);
        smallObjectsCache.invalidate(key, fullExpire);
    }

    @Override
    public void remove(String key) {
        mainCache.remove(key);
        smallObjectsCache.remove(key);
    }

    @Override
    public void clear() {
        mainCache.clear();
        smallObjectsCache.clear();
    }
}
