package com.trevore.joni.usage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.trevore.common.User;
import com.trevore.common.UsersListAdapter;
import com.trevore.joni.rx.RetainedObservableFactory;
import com.trevore.trevor.R;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class UsersListActivity extends AppCompatActivity {

    private RecyclerView list;
    private View progressBarContainer;
    private View errorTextContainer;
    private TextView errorText;

    private UsersListAdapter adapter;

    private CompositeSubscription subscriptions = new CompositeSubscription();

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

        Observable<List<User>> observable = createObservable();//cached Observable
//        Observable<List<User>> observable = UserListNetwork.createGetUserListObservable();//not cached Observable
        subscriptions.add(getSubscription(observable));
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    private Observable<List<User>> createObservable() {
        return new RetainedObservableFactory<List<User>>(getSupportFragmentManager())
                .getRetainedObservable(UserListNetwork.URL_GET_USER, UserListNetwork::createGetUserListObservable, observable -> {
                    Log.d("debug", "cache hit for " + UserListNetwork.URL_GET_USER);
                    return observable;
                });
    }

    private Subscription getSubscription(Observable<List<User>> observable) {
        return observable.subscribe(new Observer<List<User>>() {
            @Override
            public void onCompleted() {
                progressBarContainer.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable error) {
                onLoadError(error);
            }

            @Override
            public void onNext(List<User> response) {
                list.setVisibility(View.VISIBLE);
                adapter.setUsers(response);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!subscriptions.isUnsubscribed()) {
            subscriptions.unsubscribe();
        }
    }

    private void onLoadError(Throwable e) {
        list.setVisibility(View.GONE);
        progressBarContainer.setVisibility(View.GONE);
        errorTextContainer.setVisibility(View.VISIBLE);

        errorText.setText("An error occurred: " + e.getMessage());
    }
}
