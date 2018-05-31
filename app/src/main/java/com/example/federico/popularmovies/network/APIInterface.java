package com.example.federico.popularmovies.network;

import com.example.federico.popularmovies.model.Movies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {


    @GET(NetworkUtils.AUTH + "{object}/" + "{sortBy}")
    Call<Movies> getMovies(@Path("object") String object,
                           @Path("sortBy") String sortBy,
                           @Query("api_key") String apiKey);

}
