package com.trevore.rodrigo.rx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.HashMap;

import rx.Observable;

public class CacheFragment extends Fragment {

    public static final String TAG = "com.kayak.android.CacheFragment.TAG";

    private HashMap<String, CacheWrapper> mObservableMap = new HashMap<>();

    private long cacheLifeTime = Long.MAX_VALUE;

    public void setCacheLifeTime(long cacheLifeTime) {
        this.cacheLifeTime = cacheLifeTime;
    }

    private static class CacheWrapper {
        final long       startTime;
        final Observable observable;

        public CacheWrapper(long startTime, Observable observable) {
            this.observable = observable;
            this.startTime = startTime;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public <T> Observable<T> getObservable(@Nullable String key) {
        if (!TextUtils.isEmpty(key)) {
            CacheWrapper observable = mObservableMap.get(key);
            if (observable != null && !isExpired(observable.startTime)) {
                return (Observable<T>) observable.observable;
            }
        }
        return null;
    }

    private boolean isExpired(long startTime) {
        return (System.currentTimeMillis() - startTime) >= cacheLifeTime;
    }

    public <T> void putObservable(@Nullable String key, Observable<T> observable) {
        if (!TextUtils.isEmpty(key)) {
            mObservableMap.put(key, new CacheWrapper(System.currentTimeMillis(), observable));
        }
    }

    @Override
    public void onDestroy() {
        clearCache();
        super.onDestroy();
    }

    public void clearCache() {
        mObservableMap.clear();
    }

    public void clearCache(String key) {
        mObservableMap.remove(key);
    }
}