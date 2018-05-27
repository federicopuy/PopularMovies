package com.example.federico.popularmovies.network;

import android.content.Context;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context mContext) {

        if (retrofit==null){

            retrofit = new Retrofit.Builder()
                    .baseUrl(NetworkUtils.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}