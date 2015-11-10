package com.trevore.shreesh.UI;

import android.support.v4.app.FragmentManager;

public interface PresenterCreator<T> {
    T create(FragmentManager fragmentManager);
}
