package com.trevore.simplerxjavaandretrofit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.trevore.common.User;
import com.trevore.common.UsersListAdapter;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UsersListActivity extends AppCompatActivity {

    private RecyclerView list;
    private View progressBarContainer;
    private View errorTextContainer;
    private TextView errorText;

    private UsersListAdapter adapter;

    private Subscription usersListSubscription;

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

        UsersListNetworkFragment usersListNetworkFragment = getNetworkFragment();
        if (usersListNetworkFragment == null) {
            usersListNetworkFragment = new UsersListNetworkFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(usersListNetworkFragment, UsersListNetworkFragment.TAG)
                    .commit();
        }

        usersListSubscription = usersListNetworkFragment.getUserList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UsersListSubscriber());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (usersListSubscription != null && !usersListSubscription.isUnsubscribed()) {
            usersListSubscription.unsubscribe();
        }
    }

    public UsersListNetworkFragment getNetworkFragment() {
        return (UsersListNetworkFragment) getSupportFragmentManager().findFragmentByTag(UsersListNetworkFragment.TAG);
    }

    private class UsersListSubscriber extends Subscriber<List<User>> {

        @Override
        public void onCompleted() {
            progressBarContainer.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError(Throwable e) {
            list.setVisibility(View.GONE);
            progressBarContainer.setVisibility(View.GONE);
            errorTextContainer.setVisibility(View.VISIBLE);

            errorText.setText("An error occurred: " + e.getMessage());
        }

        @Override
        public void onNext(List<User> users) {
            adapter.setUsers(users);
        }
    }
}
