package com.trevore.anotherfromrodrigo.rx;

import android.app.Activity;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A helper class for executing network requests through RxJava. Subscriptions of request are kept in provided SubscriptionPool, which should unsubscribe when onDestroy
 * Created by rtang on 04/08/15.
 */
public class RxNetHandler<T> {
    private RxLoadingCallBack mLoading;
    private SubscriptionPool mSubscriptionPool;//Keeps all subscriptions, which should call unsubscribe() at the end of lifecycle of the Activity or Fragment
    private FragmentManager mFragmentManager;
    private RxNetListener<T> mListener;

    public static <T> RxNetHandler<T> newInstance(SubscriptionPool subscriptionPool) {
        RxNetHandler<T> instance = new RxNetHandler<>();
        instance.mSubscriptionPool = subscriptionPool;
        return instance;
    }

    public static <T> RxNetHandler<T> newCachableInstance(SubscriptionPool subscriptionPool, FragmentManager fragmentManager) {
        RxNetHandler<T> instance = new RxNetHandler<>();
        instance.mSubscriptionPool = subscriptionPool;
        instance.mFragmentManager = fragmentManager;
        return instance;

    }

    private RxNetHandler() {
    }

    private CacheFragment getCacheFragment(FragmentManager manager) {
        String fragmentTag = CacheFragment.class.getName();
        CacheFragment fragment = findCacheFragment(fragmentTag, manager);
        if (fragment == null) {
            fragment = new CacheFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(fragment, fragmentTag);
            transaction.commit();
        }
        return fragment;
    }

    private CacheFragment findCacheFragment(String tag, FragmentManager manager) {
        return (CacheFragment) manager.findFragmentByTag(tag);
    }

    public RxNetHandler setLoading(RxLoadingCallBack loading) {
        mLoading = loading;
        return this;
    }

    public void clearAllCache() {
        if (mFragmentManager != null) {
            CacheFragment cacheFragment = getCacheFragment(mFragmentManager);
            cacheFragment.clearCache();
        }
    }

    public void clearCache(String key) {
        if (mFragmentManager != null) {
            CacheFragment cacheFragment = getCacheFragment(mFragmentManager);
            cacheFragment.clearCache(key);
        }
    }

    /**
     * Start a network request which binds lifecycle with given Fragment. Former working request will be reused if exists, and the subscription will be updated.
     */
    public void start(Fragment fragment, RxNetRequest<T> rxRequest) {
        onStart(fragment.getActivity());
        Observable<T> observable = getCachedObservableOrCreate(rxRequest);

        mSubscriptionPool.addSubscription(getSubscription(observable));
    }

    /**
     * Start a network request which binds lifecycle with given Activity. Former working request will be reused if exists, and the subscription will be updated.
     */
    public void start(Activity activity, RxNetRequest<T> rxRequest) {
        onStart(activity);
        Observable<T> observable = getCachedObservableOrCreate(rxRequest);

        mSubscriptionPool.addSubscription(getSubscription(observable));
    }

    @NonNull
    private Observable<T> getCachedObservableOrCreate(RxNetRequest<T> rxRequest) {
        Observable<T> observable = null;
        CacheFragment cacheFragment = null;
        if (mFragmentManager != null) {
            cacheFragment = getCacheFragment(mFragmentManager);
            cacheFragment.setCacheLifeTime(rxRequest.getCacheLiftTime());

            observable = cacheFragment.getObservable(rxRequest.getCacheKey());
        }
        if (observable == null) {
            observable = rxRequest.execute()
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .cache();
            if (mFragmentManager != null && cacheFragment != null) {
                cacheFragment.putObservable(rxRequest.getCacheKey(), observable);
            }
        }
        return observable;
    }

    private void onStart(final Activity activity) {
        if (mLoading != null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                mLoading.onStart();
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onStart(activity);
                    }
                });
            }
        }
    }

    public void setListener(RxNetListener<T> listener) {
        this.mListener = listener;
    }

    private Subscription getSubscription(Observable<T> observable) {
        return observable.subscribe(new Observer<T>() {
            @Override
            public void onCompleted() {
                if (mLoading != null) {
                    mLoading.onFinish();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (mLoading != null) {
                    mLoading.onFinish();
                }
                if (mListener != null) {
                    mListener.onError(throwable);
                }
            }

            @Override
            public void onNext(T response) {
                if (mListener != null) {
                    mListener.onResult(response);
                }
            }
        });
    }
}
