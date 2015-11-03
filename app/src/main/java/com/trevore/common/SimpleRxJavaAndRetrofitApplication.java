package com.trevore.common;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by trevor on 10/29/15.
 */
public class SimpleRxJavaAndRetrofitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);
        Stetho.initializeWithDefaults(this);
    }
}
