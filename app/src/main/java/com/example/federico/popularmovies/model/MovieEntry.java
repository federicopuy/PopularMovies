package com.example.federico.popularmovies.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


@Entity(tableName = "movie")
public class MovieEntry {


    @PrimaryKey(autoGenerate = true)
    private int idRoom;

    @SerializedName("id")
    @Expose
    private Integer idMovieDb;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;




    /**
     * No args constructor for use in serialization
     *
     */
    @Ignore
    public MovieEntry() {
    }

    /**
     *
     * @param idMovieDb
     * @param title
     * @param releaseDate
     * @param overview
     * @param posterPath
     * @param voteAverage
     */


    @Ignore
    public MovieEntry(Integer idMovieDb, Double voteAverage, String title, String posterPath, String overview, String releaseDate) {
        this.idMovieDb = idMovieDb;
        this.voteAverage = voteAverage;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    public MovieEntry(int idRoom, Integer idMovieDb, Double voteAverage, String title, String posterPath, String overview, String releaseDate) {
        this.idRoom = idRoom;
        this.idMovieDb = idMovieDb;
        this.voteAverage = voteAverage;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }


    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int id) {
        this.idRoom = id;
    }

    public Integer getIdMovieDb() {
        return idMovieDb;
    }

    public void setIdMovieDb(Integer idMovieDb) {
        this.idMovieDb = idMovieDb;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}