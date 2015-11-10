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
    private UserListNetworkConsumer userListNetworkConsumer;
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

        userListNetworkConsumer = new UserListNetworkConsumer();

        Observable<List<User>> observable = createUserListObservable();
        Subscription userListSubscription = observable.subscribe(new UserListObserver());
        subscriptions.add(userListSubscription);
    }

    private Observable<List<User>> createUserListObservable() {
        return new RetainedObservableFactory<List<User>>(this)
                .getRetainedObservable(UserListNetworkConsumer.KEY_USER_LIST,
                        userListNetworkConsumer.createGetUserListObservable(),
                        observable -> {
                            Log.d("debug", "cache hit for " + UserListNetworkConsumer.KEY_USER_LIST);
                            return observable;
                        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!subscriptions.isUnsubscribed()) {
            subscriptions.unsubscribe();
        }
    }

    private class UserListObserver implements Observer<List<User>> {
        @Override
        public void onCompleted() {
            progressBarContainer.setVisibility(View.GONE);
        }

        @Override
        public void onError(Throwable error) {
            list.setVisibility(View.GONE);
            progressBarContainer.setVisibility(View.GONE);
            errorTextContainer.setVisibility(View.VISIBLE);

            errorText.setText("An error occurred: " + error.getMessage());
        }

        @Override
        public void onNext(List<User> response) {
            list.setVisibility(View.VISIBLE);
            adapter.setUsers(response);
        }
    }
}
