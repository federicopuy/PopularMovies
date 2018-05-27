package com.example.federico.popularmovies.utils;

import com.example.federico.popularmovies.network.NetworkUtils;

import java.util.ArrayList;
import java.util.List;


public class Utils {

    public static List<CharSequence> getSortOptions (){

        List<CharSequence> sortOptions = new ArrayList<>();

        sortOptions.add(Constants.SORT_BY_POPULARITY);
        sortOptions.add(Constants.SORT_BY_RATING);

        return sortOptions;

    }

    public static String getSortParameterURL(String sortOption) {

        switch (sortOption) {

            case Constants.SORT_BY_POPULARITY:
                return Constants.SORT_BY_POPULARITY_URL;

            case Constants.SORT_BY_RATING:
                return Constants.SORT_BY_RATING_URL;

            default:
                return null;
        }

    }

    public static String getImageUrl (String imagePath){

        return NetworkUtils.IMAGES_URL + NetworkUtils.IMAGES_SIZE + imagePath;

    }

}
