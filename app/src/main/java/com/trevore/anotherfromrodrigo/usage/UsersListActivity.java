package com.trevore.anotherfromrodrigo.usage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.trevore.anotherfromrodrigo.rx.RxLoadingCallBack;
import com.trevore.anotherfromrodrigo.rx.RxNetHandler;
import com.trevore.anotherfromrodrigo.rx.RxNetListener;
import com.trevore.anotherfromrodrigo.rx.RxNetRequest;
import com.trevore.anotherfromrodrigo.rx.SubscriptionPool;
import com.trevore.simplerxjavaandretrofit.R;
import com.trevore.common.User;
import com.trevore.common.UserListService;
import com.trevore.common.UsersListAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class UsersListActivity extends AppCompatActivity implements SubscriptionPool {

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

        RxNetHandler<List<User>> rxNetHandler = RxNetHandler.newCachableInstance(this, getSupportFragmentManager());
        rxNetHandler.setLoading(new RxLoadingCallBack() {
            @Override
            public void onStart() {
                progressBarContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                progressBarContainer.setVisibility(View.GONE);
            }
        });
        rxNetHandler.setListener(new RxNetListener<List<User>>() {
            @Override
            public void onResult(List<User> response) {
                list.setVisibility(View.VISIBLE);
                adapter.setUsers(response);
            }

            @Override
            public void onError(Throwable error) {
                onLoadError(error);
            }
        });
        rxNetHandler.start(this, new RxNetRequest<List<User>>() {
            @Override
            public Observable<List<User>> execute() {
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

            @Nullable
            @Override
            public String getCacheKey() {
                return URL;
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

    @Override
    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    private void onLoadError(Throwable e) {
        list.setVisibility(View.GONE);
        progressBarContainer.setVisibility(View.GONE);
        errorTextContainer.setVisibility(View.VISIBLE);

        errorText.setText("An error occurred: " + e.getMessage());
    }
}
