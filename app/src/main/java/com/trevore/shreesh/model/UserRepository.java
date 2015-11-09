package com.trevore.shreesh.model;

import com.trevore.common.User;

import java.util.List;

import rx.Observable;

public interface UserRepository {

    Observable<List<User>> getUsers();
}
