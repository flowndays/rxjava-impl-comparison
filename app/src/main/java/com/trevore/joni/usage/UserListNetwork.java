package com.trevore.joni.usage;

import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.trevore.common.User;
import com.trevore.common.UserListService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;

/**
 * Created by rtang on 10/11/15.
 */
public class UserListNetwork {
    public static final String URL_GET_USER = "http://jsonplaceholder.typicode.com";

    @NonNull
    public static Observable<List<User>> createGetUserListObservable() {
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_GET_USER)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        UserListService service = retrofit.create(UserListService.class);
        return service.listUsers().delay(3, TimeUnit.SECONDS);
    }

    public static Observable<Object> createOtherRequestObservable() {
        return null;
    }
}
