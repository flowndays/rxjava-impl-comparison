package com.trevore.shreesh.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import rx.Subscription;

/**
 * Can be swapped later on, to not be a retained fragment.
 */
public class BasePresenter extends Fragment {
    protected Subscription userSubscription;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribe();
    }

    public void unsubscribe() {
        if (userSubscription != null) {
            userSubscription.unsubscribe();
        }
    }
}
