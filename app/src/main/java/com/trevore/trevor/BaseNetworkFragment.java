package com.trevore.trevor;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by trevor on 11/2/15.
 */
public abstract class BaseNetworkFragment extends Fragment {

    public static final String TAG = "BaseNetworkFragment.TAG";

    public BaseNetworkFragment() {
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        createServices(retrofit);
    }

    protected abstract void createServices(Retrofit retrofit);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }
}
