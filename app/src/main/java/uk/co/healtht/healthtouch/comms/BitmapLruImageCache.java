package uk.co.healtht.healthtouch.comms;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * Basic LRU Memory cache.
 *
 */
public class BitmapLruImageCache extends LruCache<String, Bitmap> implements ImageCache {

    private final String TAG = this.getClass().getSimpleName();

    public BitmapLruImageCache(int maxSize) {
        super(maxSize);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected int sizeOf(String key, Bitmap value) {
        if (Build.VERSION.SDK_INT >= 19) {
            return value.getAllocationByteCount();
        }
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}