package com.trevore.joni.rx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.HashMap;

public class CacheFragment<T> extends Fragment {

    public static final String TAG = "com.kayak.android.CacheFragment.TAG";

    private HashMap<Object, CacheWrapper<T>> cache = new HashMap<>();

    private long cacheLifeTime = Long.MAX_VALUE;

    public void setCacheLifeTime(long cacheLifeTime) {
        this.cacheLifeTime = cacheLifeTime;
    }

    private static class CacheWrapper<T> {
        final long startTime;
        final T value;

        public CacheWrapper(long startTime, T value) {
            this.value = value;
            this.startTime = startTime;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public T get(@Nullable Object key) {
        CacheWrapper<T> wrapper = cache.get(key);
        if (wrapper != null && !isExpired(wrapper.startTime)) {
            return wrapper.value;
        }
        return null;
    }

    private boolean isExpired(long startTime) {
        return (System.currentTimeMillis() - startTime) >= cacheLifeTime;
    }

    public void put(@Nullable Object key, T observable) {
        if (key != null) {
            cache.put(key, new CacheWrapper<>(System.currentTimeMillis(), observable));
        }
    }

    @Override
    public void onDestroy() {
        clearCache();
        super.onDestroy();
    }

    public void clearCache() {
        cache.clear();
    }

    public void clearCache(Object key) {
        cache.remove(key);
    }
}