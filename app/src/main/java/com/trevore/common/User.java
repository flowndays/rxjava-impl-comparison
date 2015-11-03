package com.trevore.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by trevor on 10/8/15.
 */
public class User {

    @SerializedName("name")
    public final String name;

    @SerializedName("email")
    public final String email;

    private User() {
        this.name = null;
        this.email = null;
    }
}
