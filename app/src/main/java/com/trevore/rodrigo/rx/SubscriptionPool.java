package com.trevore.rodrigo.rx;

import rx.Subscription;

/**
 * Created by rtang on 05/08/15.
 */
public interface SubscriptionPool {
    void addSubscription(Subscription subscription);
}
