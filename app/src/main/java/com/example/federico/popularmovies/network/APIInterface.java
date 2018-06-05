package com.example.federico.popularmovies.network;

import com.example.federico.popularmovies.model.Movies;
import com.example.federico.popularmovies.model.MovieEntry;
import com.example.federico.popularmovies.model.Review;
import com.example.federico.popularmovies.model.Video;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {


    @GET(NetworkUtils.AUTH + "{object}/" + "{sortBy}")
    Call<Movies<MovieEntry>> getMovies(@Path("object") String object,
                                       @Path("sortBy") String sortBy,
                                       @Query("api_key") String apiKey);

    @GET(NetworkUtils.AUTH + "{object}/" + "{movieId}/" + "{type}")
    Call<Movies<Video>> getVideos(@Path("object") String object,
                                  @Path("movieId") String movieId,
                                  @Path("type") String type,
                                  @Query("api_key") String apiKey);

    @GET(NetworkUtils.AUTH + "{object}/" + "{movieId}/" + "{type}")
    Call<Movies<Review>> getReviews(@Path("object") String object,
                                    @Path("movieId") String movieId,
                                    @Path("type") String type,
                                    @Query("api_key") String apiKey);





}
