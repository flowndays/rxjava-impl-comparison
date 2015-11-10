package com.trevore.shreesh.model;

import com.trevore.common.User;
import com.trevore.common.UserListService;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NetworkUserRepository implements UserRepository {
    private final UserListService userListService;

    public NetworkUserRepository(UserListService userListService) {
        this.userListService = userListService;
    }

    @Override
    public Observable<List<User>> getUsers() {
        return userListService.listUsers().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
