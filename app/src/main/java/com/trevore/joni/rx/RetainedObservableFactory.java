package com.trevore.joni.rx;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Observable provider with a cache mechanism. If you don't need to cache the value, create it directly.
 * Created by rtang on 15/10/15.
 */
public class RetainedObservableFactory<T> {
    public static final String TAG = "com.kayak.android.common.net.RetainedObservableFactory";
    private static final long SEARCH_TIMEOUT = TimeUnit.MINUTES.toMillis(20);

    private final FragmentManager mFragmentManager;

    public RetainedObservableFactory(@NonNull FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    /**
     * Try to get cached Observable, if it doesn't exist, create a new one and put it into cache system.
     *
     * cacheReadTransform is called when cached Observable is reused.
     * You can transform the original Observable, e.g: filter.
     *
     */
    public Observable<T> getRetainedObservable(Object cacheKey, Func0<Observable<T>> observableFactory, Func1<Observable<T>, Observable<T>> cacheReadTransform) {
        Observable<T> observable;
        CacheFragment<Observable<T>> cacheFragment;
        cacheFragment = getCacheFragment();

        observable = cacheFragment.get(cacheKey);
        if (observable == null) {
            observable = observableFactory.call();
            observable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

            observable = observable.cache();
            cacheFragment.put(cacheKey, observable);
        } else {
            observable = cacheReadTransform==null? observable: cacheReadTransform.call(observable);
        }

        return observable;
    }

    public Observable<T> getRetainedObservable(Object cacheKey, Func0<Observable<T>> observableFactory) {
        return getRetainedObservable(cacheKey, observableFactory, null);
    }

    private CacheFragment<Observable<T>> getCacheFragment() {
        Fragment fragment = mFragmentManager.findFragmentByTag(TAG);
        CacheFragment cacheFragment;
        if (fragment == null) {
            cacheFragment = new CacheFragment();
            mFragmentManager.beginTransaction().add(cacheFragment, TAG).commit();
        } else {
            cacheFragment = (CacheFragment) fragment;
        }
        cacheFragment.setCacheLifeTime(SEARCH_TIMEOUT);
        return (CacheFragment<Observable<T>>) cacheFragment;
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
