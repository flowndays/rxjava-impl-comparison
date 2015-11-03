package com.trevore.rodrigo.rx;

import android.support.annotation.MainThread;

/**
 * Created by rtang on 24/08/15.
 */
public interface RxNetListener<T> {
    @MainThread
    void onResult(T response);

    @MainThread
    void onError(Throwable error);
}
