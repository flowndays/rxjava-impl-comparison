package com.trevore.trevor;

import com.trevore.common.User;
import com.trevore.common.UserListService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.Retrofit;
import rx.Observable;

/**
 * Created by trevor on 10/8/15.
 */
public class UsersListNetworkFragment extends BaseNetworkFragment {

    private UserListService service;

    private Observable<List<User>> usersListObservable;

    @Override
    protected void createServices(Retrofit retrofit) {
        service = retrofit.create(UserListService.class);
    }

    public Observable<List<User>> getUserList() {
        if (usersListObservable == null) {
            usersListObservable = service.listUsers()
                    .delay(3, TimeUnit.SECONDS)
                    .cache();
        }

        return usersListObservable;
    }
}
