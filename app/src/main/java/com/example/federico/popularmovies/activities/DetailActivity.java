package com.example.federico.popularmovies.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.federico.popularmovies.BuildConfig;
import com.example.federico.popularmovies.R;
import com.example.federico.popularmovies.adapters.ReviewsAdapter;
import com.example.federico.popularmovies.adapters.VideosAdapter;
import com.example.federico.popularmovies.database.AppDatabase;
import com.example.federico.popularmovies.model.MovieEntry;
import com.example.federico.popularmovies.model.Movies;
import com.example.federico.popularmovies.model.Review;
import com.example.federico.popularmovies.model.Video;
import com.example.federico.popularmovies.network.APIInterface;
import com.example.federico.popularmovies.network.NetworkUtils;
import com.example.federico.popularmovies.network.RetrofitClient;
import com.example.federico.popularmovies.utils.AppExecutors;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements VideosAdapter.ListItemClickListener {


    private static final String TAG = "Detail Activity";
    private static boolean FLAG_IS_FAVORITE = false;
    @BindView(R.id.fabFavorite)
    FloatingActionButton fabFavorite;
    private RecyclerView videosRecyclerView;
    private RecyclerView reviewsRecyclerView;
    private APIInterface mService;
    private com.example.federico.popularmovies.model.MovieEntry movieEntry;
    private VideosAdapter videosAdapter;
    private ReviewsAdapter reviewsAdapter;
    private AppDatabase mDb;
    private ProgressBar progressBar;

    /*-------------------------------------- OnCreate ----------------------------------------------***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        setupUI();

        if (!NetworkUtils.isNetworkConnected(DetailActivity.this)){

            Toast.makeText(DetailActivity.this, R.string.check_internet, Toast.LENGTH_LONG).show();

        } else{

            getMovieInfo(movieEntry);

        }

        mDb = AppDatabase.getInstance(getApplicationContext());

        DetailViewModelFactory factory = new DetailViewModelFactory(mDb, movieEntry.getIdMovieDb());

        final DetailViewModel viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel.class);

        viewModel.getMovieEntry().observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry movieEntryOnChanged) {

                if (movieEntryOnChanged != null) {

                    movieEntry = movieEntryOnChanged;
                    fabFavorite.setImageResource(R.drawable.ic_baseline_star_24px_golden);
                    //used FLAG to know if the movie has already been marked as favorite
                    FLAG_IS_FAVORITE = true;

                } else {

                    FLAG_IS_FAVORITE = false;

                }
            }
        });

    }

    /*-------------------------------------- Setup UI  --------------------------------------------***/

    private void setupUI() {

        // did not use Butterknife to instantiate these views as I could not find a way to access
        // them through contentView using the library.

        View contentView = findViewById(R.id.content_view);

        TextView tvPlotSynopsis = contentView.findViewById(R.id.tvPlotSynopsis);

        TextView tvReleaseDate = contentView.findViewById(R.id.tvReleaseDate);

        TextView tvVoteAverage = contentView.findViewById(R.id.tvVoteAverage);

        ImageView imagePoster = contentView.findViewById(R.id.imagePoster);

        progressBar = contentView.findViewById(R.id.progressBar);

        setupRecyclerViews();

        Intent intent = getIntent();

        if (intent.hasExtra(MainActivity.DETAIL_MOVIE_INTENT)) {

            String movieJson = intent.getStringExtra(MainActivity.DETAIL_MOVIE_INTENT);

            Gson movieGson = new Gson();

            movieEntry = movieGson.fromJson(movieJson, com.example.federico.popularmovies.model.MovieEntry.class);

            String imagePath = movieEntry.getPosterPath();

            String imageURL = NetworkUtils.IMAGES_URL + NetworkUtils.IMAGES_SIZE + imagePath;

            Context context = DetailActivity.this;

            int width = context.getResources().getDisplayMetrics().widthPixels;

            Picasso.get().load(imageURL)
                    .centerCrop()
                    .resize(width / 3, width / 2)
                    .error(R.drawable.ic_baseline_error_24px)
                    .into(imagePoster);

            Objects.requireNonNull(getSupportActionBar()).setTitle(movieEntry.getTitle());

            tvPlotSynopsis.setText(movieEntry.getOverview());

            tvReleaseDate.setText(movieEntry.getReleaseDate());

            tvVoteAverage.setText(String.valueOf(movieEntry.getVoteAverage()));

        }
    }

    /*-------------------------------------- SetupRecyclerViews ------------------------------------***/

    private void setupRecyclerViews() {

        // video recycler view

        videosRecyclerView = findViewById(R.id.videos_recycler_view);

        RecyclerView.LayoutManager videoLayoutManager = new LinearLayoutManager(this);

        videosRecyclerView.setLayoutManager(videoLayoutManager);

        // video recycler view

        reviewsRecyclerView = findViewById(R.id.reviews_recycler_view);

        RecyclerView.LayoutManager reviewLayoutManager = new LinearLayoutManager(this);

        reviewsRecyclerView.setLayoutManager(reviewLayoutManager);

    }


    /*-------------------------------------- getMovieInfo (API Request) ---------------------------------------------***/

    private void getMovieInfo(final MovieEntry movieEntry) {

        String movieId = "";

        try {
            movieId = String.valueOf(movieEntry.getIdMovieDb());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mService = RetrofitClient.getClient(getApplicationContext()).create(APIInterface.class);

        Call<Movies<Video>> callGetMovieVideos = mService.getVideos(NetworkUtils.OBJECT, movieId, NetworkUtils.VIDEOS, BuildConfig.MY_MOVIE_DB_API_KEY);

        callGetMovieVideos.enqueue(new Callback<Movies<Video>>() {
            @Override
            public void onResponse(Call<Movies<Video>> call, Response<Movies<Video>> response) {

                progressBar.setVisibility(View.VISIBLE);

                if (response.isSuccessful()) {

                    try {

                        Movies<Video> movies = response.body();

                        assert movies != null;
                        List<Video> videoList = movies.getResults();

                        videosAdapter = new VideosAdapter(videoList, DetailActivity.this, DetailActivity.this);

                        videosRecyclerView.setAdapter(videosAdapter);

                        videosRecyclerView.addItemDecoration(new DividerItemDecoration(videosRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

                        getReviews(String.valueOf(movieEntry.getIdMovieDb()));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {

                    progressBar.setVisibility(View.INVISIBLE);

                    try {
                        Log.i(TAG, Objects.requireNonNull(response.errorBody()).string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(DetailActivity.this, R.string.error, Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<Movies<Video>> call, Throwable t) {

                progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(DetailActivity.this, R.string.error, Toast.LENGTH_LONG).show();

                Log.e(TAG, t.getMessage());

            }
        });

    }

    /*-------------------------------------- ListItemClickVideos -----------------------------------***/

    @Override
    public void onListItemClick(int clickedItemIndex, Video videoClicked) {

        NetworkUtils.watchYoutubeVideo(DetailActivity.this, videoClicked.getKey());

    }

    /*-------------------------------------- getReviews --------------------------------------------***/

    private void getReviews(String movieId) {

        Call<Movies<Review>> callGetReviews = mService.getReviews(NetworkUtils.OBJECT, movieId, NetworkUtils.REVIEWS, BuildConfig.MY_MOVIE_DB_API_KEY);

        callGetReviews.enqueue(new Callback<Movies<Review>>() {
            @Override
            public void onResponse(Call<Movies<Review>> call, Response<Movies<Review>> response) {

                progressBar.setVisibility(View.INVISIBLE);

                if (response.isSuccessful()) {

                    try {

                        Movies<Review> movies = response.body();

                        assert movies != null;
                        List<Review> reviewList = movies.getResults();

                        reviewsAdapter = new ReviewsAdapter(reviewList, DetailActivity.this);

                        reviewsRecyclerView.setAdapter(reviewsAdapter);

                        reviewsRecyclerView.addItemDecoration(new DividerItemDecoration(reviewsRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    try {
                        Log.i(TAG, Objects.requireNonNull(response.errorBody()).string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(DetailActivity.this, R.string.error, Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<Movies<Review>> call, Throwable t) {

                progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(DetailActivity.this, R.string.error, Toast.LENGTH_LONG).show();

            }
        });

    }

    /*-------------------------------------- OnClick Star --------------------------------------------***/

    @OnClick(R.id.fabFavorite)
    void addMovieToDb() {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                if (FLAG_IS_FAVORITE) {

                    mDb.movieDAO().deleteMovie(movieEntry);
                    fabFavorite.setImageResource(R.drawable.ic_baseline_star_24px);

                } else {

                    mDb.movieDAO().insertMovie(movieEntry);

                }

            }
        });
    }

}

