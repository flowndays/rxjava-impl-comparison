package com.trevore.joni.usage;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by Trevor Elkins on 11/10/15.
 */
public abstract class BaseNetworkConsumer {

    public BaseNetworkConsumer() {
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        createServices(retrofit);
    }

    protected abstract String getUrl();
    protected abstract void createServices(Retrofit retrofit);
}
