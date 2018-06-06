package com.example.federico.popularmovies.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.federico.popularmovies.model.MovieEntry;

import java.util.List;

@Dao
public interface MovieDAO {

    @Query("SELECT * FROM movie")
    LiveData<List<MovieEntry>> loadAllMovies();

    @Insert
    void insertMovie(MovieEntry moviesEntry);

    @Delete
    void deleteMovie(MovieEntry moviesEntry);

    @Query("SELECT * FROM movie WHERE idMovieDb = :id")
    LiveData<MovieEntry> loadMovieById(int id);

}
