package com.trevore.rodrigo.rx;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import rx.Observable;

/**
 * Created by rtang on 04/08/15.
 */
public abstract class RxNetRequest<T> {
    public static final int CACHE_LIFE_TIME = 1000;

    @WorkerThread
    public abstract Observable<T> execute();

    /**
     * Create a cache key to cache request results. Only valid when {@link RxNetHandler#mFragmentManager} is not null.
     *
     * @return unique cache key for this request
     */
    @Nullable
    public String getCacheKey() {
        return null;
    }

    public long getCacheLiftTime() {
        return CACHE_LIFE_TIME;
    }
}
