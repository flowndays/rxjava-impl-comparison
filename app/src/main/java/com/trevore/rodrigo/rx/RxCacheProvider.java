package com.trevore.rodrigo.rx;

import android.support.annotation.NonNull;

import rx.Observable;

/**
 * Helper class for Observable cache system.
 * Created by rtang on 15/10/15.
 */
public abstract class RxCacheProvider<T> {
    @NonNull
    public abstract String createCacheKey();

    public abstract Observable<T> createOriginalObservable();

    /**
     * Called when cached Observable is reused.
     * You can transform the original Observable, e.g: filter.
     *
     * @param observable original Observable
     * @return transformed Observable.
     */
    public Observable<T> transformCache(Observable<T> observable) {
        return observable;
    }
}
