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
public class UserListNetworkConsumer extends BaseNetworkConsumer {

    public static final String KEY_USER_LIST = "UserListNetworkConsumer.KEY_USER_LIST";

    private static final String URL_GET_USER = "http://jsonplaceholder.typicode.com";

    private UserListService userListService;

    @Override
    protected String getUrl() {
        return URL_GET_USER;
    }

    @Override
    protected void createServices(Retrofit retrofit) {
        userListService = retrofit.create(UserListService.class);
    }

    @NonNull
    public Observable<List<User>> createGetUserListObservable() {
        return userListService.listUsers().delay(3, TimeUnit.SECONDS);
    }

    public Observable<Object> createOtherRequestObservable() {
        return null;
    }
}
