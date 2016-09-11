package com.misael.example.test.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MISAEL on 11/09/2016.
 *
 */
public class Beacon {

    @SerializedName("data")
    private Data data;

    @SerializedName("id")
    private int id;

    @SerializedName("success")
    private boolean success;

    public int getId() {
        return id;
    }

    public Data getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }
}
