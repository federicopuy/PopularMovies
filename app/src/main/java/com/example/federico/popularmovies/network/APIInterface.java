package com.example.federico.popularmovies.network;

import com.example.federico.popularmovies.model.Movies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {


    @GET(NetworkUtils.AUTH + "{method}/" + "{object}")
    Call<Movies> getMovies(@Path("method") String method,
                           @Path("object") String object,
                           @Query("api_key") String apiKey, @Query("sort_by") String sortBy);

}
