package com.trevore.shreesh.model;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.trevore.common.UserListService;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class UserRepositories {
    private static UserRepositories INSTANCE;
    private Retrofit retrofit;
    private UserRepository userRepository;

    public UserRepositories() {
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());

        retrofit = new Retrofit.Builder().baseUrl("http://jsonplaceholder.typicode.com")
                                         .client(client)
                                         .addConverterFactory(GsonConverterFactory.create())
                                         .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                         .build();
    }

    public static UserRepository getUserListRepository() {
        UserRepositories instance = getInstance();
        if (instance.userRepository == null) {
            instance.userRepository = new NetworkUserRepository(instance.retrofit.create(UserListService.class));
        }
        return instance.userRepository;
    }

    private static UserRepositories getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserRepositories();
        }
        return INSTANCE;
    }
}
