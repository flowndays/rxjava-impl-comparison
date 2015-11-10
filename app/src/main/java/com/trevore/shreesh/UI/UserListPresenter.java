package com.trevore.shreesh.UI;

import com.trevore.common.User;
import com.trevore.shreesh.model.UserRepository;

import java.util.List;

import rx.Subscriber;

public class UserListPresenter extends BasePresenter implements UserListContract.UserActionsListener {
    private UserRepository userRepository;
    private UserListContract.View userView;

    public void connect(UserRepository userRepository, UserListContract.View userView) {
        this.userRepository = userRepository;
        this.userView = userView;
    }

    @Override
    public void subscribeUsers() {
        if (userRepository == null) {
            throw new IllegalStateException("Repository is null");
        }

        userSubscription = userRepository.getUsers().subscribe(new Subscriber<List<User>>() {
            @Override
            public void onCompleted() {
                if (userView != null) {
                    userView.showUserList();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (userView != null) {
                    userView.showError(e.getLocalizedMessage());
                }
            }

            @Override
            public void onNext(List<User> users) {
                if (userView != null) {
                    userView.loadUsers(users);
                }
            }
        });
    }
}
