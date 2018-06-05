package com.example.federico.popularmovies.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.federico.popularmovies.database.AppDatabase;
import com.example.federico.popularmovies.model.MovieEntry;

class DetailViewModel extends ViewModel {

    private LiveData<MovieEntry> movieEntry;

    public DetailViewModel(AppDatabase database, int movieId) {

        movieEntry = database.movieDAO().loadMovieById(movieId);

    }

    public LiveData<MovieEntry>getMovieEntry(){
        return movieEntry;
    }
}
