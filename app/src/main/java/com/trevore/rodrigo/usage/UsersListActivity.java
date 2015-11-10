package com.trevore.rodrigo.usage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.trevore.common.User;
import com.trevore.common.UserListService;
import com.trevore.common.UsersListAdapter;
import com.trevore.rodrigo.rx.CachedObservableProvider;
import com.trevore.rodrigo.rx.RxCacheProvider;
import com.trevore.trevor.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class UsersListActivity extends AppCompatActivity {

    public static final String URL = "http://jsonplaceholder.typicode.com";
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
//        Observable<List<User>> observable = createGetUserListObservable();//not cached Observable
        subscriptions.add(getSubscription(observable));
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    private Observable<List<User>> createObservable() {
        return new CachedObservableProvider<List<User>>(getSupportFragmentManager()).getObservable(new RxCacheProvider<List<User>>() {
            @NonNull
            @Override
            public String createCacheKey() {
                return URL;
            }

            @Override
            public Observable<List<User>> createOriginalObservable() {
                return getOriginalObservable();
            }

            @Override
            public Observable<List<User>> transformCache(Observable<List<User>> observable) {
                Log.d("debug", "cache hit for " + createCacheKey());
                return super.transformCache(observable);
            }
        });
    }

    @NonNull
    private Observable<List<User>> getOriginalObservable() {
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        UserListService service = retrofit.create(UserListService.class);
        return service.listUsers().delay(3, TimeUnit.SECONDS);
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
