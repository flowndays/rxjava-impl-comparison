package com.trevore.shreesh.UI;

import com.trevore.common.User;

import java.util.List;

public class UserListContract {

    interface UserActionsListener {

        void subscribeUsers();

        void unsubscribe();
    }

    interface View {
        void showError(String error);

        void loadUsers(List<User> userList);

        void showUserList();
    }
}
