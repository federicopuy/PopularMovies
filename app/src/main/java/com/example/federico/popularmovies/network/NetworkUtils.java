package com.example.federico.popularmovies.network;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.util.Objects;

public class NetworkUtils {

    public static final String HOST = " https://api.themoviedb.org/";
    public static final String AUTH = "3/";
    public static final String OBJECT = "movie";
    public static final String IMAGES_URL = "https://image.tmdb.org/t/p/";
    public static final String IMAGES_SIZE = "w185";
    private static final String YOU_TUBE_APP_URI = "vnd.youtube:";
    private static final String YOU_TUBE_WEB_URI = "http://www.youtube.com/watch?v=";
    public static final String VIDEOS = "videos";
    public static final String REVIEWS = "reviews";


    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOU_TUBE_APP_URI + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(YOU_TUBE_WEB_URI + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
