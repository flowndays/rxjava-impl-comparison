package com.trevore.rodrigo.rx;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Observable provider with a cache mechanism. If you don't need to cache the observable, create it directly.
 * Created by rtang on 15/10/15.
 */
public class CachedObservableProvider<T> {
    public static final String TAG = "com.kayak.android.common.net.CachedObservableProvider";
    private static final long SEARCH_TIMEOUT = TimeUnit.MINUTES.toMillis(20);

    private final FragmentManager mFragmentManager;

    public CachedObservableProvider(@NonNull FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    /**
     * Try to get cached Observable, if it doesn't exist, create a new one and put it into cache system.
     */
    @NonNull
    public Observable<T> getObservable(RxCacheProvider<T> cacheProvider) {
        String cacheKey = cacheProvider.createCacheKey();
        Observable<T> observable;
        CacheFragment cacheFragment;
        cacheFragment = getCacheFragment();

        observable = cacheFragment.getObservable(cacheKey);
        if (observable == null) {
            observable = cacheProvider.createOriginalObservable();
            observable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

            observable = observable.cache();
            cacheFragment.putObservable(cacheKey, observable);
        } else {
            observable = cacheProvider.transformCache(observable);
        }

        return observable;
    }

    private CacheFragment getCacheFragment() {
        Fragment fragment = mFragmentManager.findFragmentByTag(TAG);
        CacheFragment cacheFragment;
        if (fragment == null) {
            cacheFragment = new CacheFragment();
            mFragmentManager.beginTransaction().add(cacheFragment, TAG).commit();
        } else {
            cacheFragment = (CacheFragment) fragment;
        }
        cacheFragment.setCacheLifeTime(SEARCH_TIMEOUT);
        return cacheFragment;
    }

    public void clearAllCache() {
        CacheFragment cacheFragment = getCacheFragment();
        cacheFragment.clearCache();
    }

    public void clearCache(String key) {
        CacheFragment cacheFragment = getCacheFragment();
        cacheFragment.clearCache(key);
    }

}
