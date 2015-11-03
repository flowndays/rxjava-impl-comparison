package com.trevore.common;

import java.util.List;

import retrofit.http.GET;
import rx.Observable;

/**
 * Created by trevor on 10/13/15.
 */
public interface UserListService {

    @GET("users")
    Observable<List<User>> listUsers();
}
