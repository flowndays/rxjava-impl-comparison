package com.trevore.shreesh.UI;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.trevore.common.User;
import com.trevore.common.UsersListAdapter;
import com.trevore.shreesh.model.UserRepositories;
import com.trevore.trevor.R;
import com.trevore.trevor.UsersListNetworkFragment;

import java.util.List;

public class UsersListActivity extends AppCompatActivity implements PresenterCreator<UserListPresenter>, UserListContract.View {

    private RecyclerView list;
    private View progressBarContainer;
    private View errorTextContainer;
    private TextView errorText;

    private UsersListAdapter adapter;
    private UserListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlist_activity);

        list = (RecyclerView) findViewById(R.id.list);
        progressBarContainer = findViewById(R.id.progressBar);
        errorTextContainer = findViewById(R.id.errorTextContainer);
        errorText = (TextView) findViewById(R.id.errorText);

        adapter = new UsersListAdapter();

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        presenter = create(getSupportFragmentManager());
        presenter.subscribeUsers();
    }

    @Override
    public UserListPresenter create(FragmentManager fragmentManager) {
        UserListPresenter presenter = (UserListPresenter) fragmentManager.findFragmentByTag(UsersListNetworkFragment.TAG);
        if (presenter == null) {
            presenter = new UserListPresenter();
            fragmentManager.beginTransaction().add(presenter, UsersListNetworkFragment.TAG).commit();
        }

        presenter.connect(UserRepositories.getUserListRepository(), this);
        return presenter;
    }

    @Override
    public void showError(String error) {
        errorTextContainer.setVisibility(View.VISIBLE);
        progressBarContainer.setVisibility(View.GONE);
        errorText.setText(error);
    }

    @Override
    public void loadUsers(List<User> userList) {
        adapter.setUsers(userList);
    }

    @Override
    public void showUserList() {
        list.setVisibility(View.VISIBLE);
        progressBarContainer.setVisibility(View.GONE);
    }
}
