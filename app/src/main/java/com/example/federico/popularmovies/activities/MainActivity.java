package com.example.federico.popularmovies.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.federico.popularmovies.BuildConfig;
import com.example.federico.popularmovies.R;
import com.example.federico.popularmovies.adapters.CustomAdapter;
import com.example.federico.popularmovies.model.MovieEntry;
import com.example.federico.popularmovies.model.Movies;
import com.example.federico.popularmovies.network.APIInterface;
import com.example.federico.popularmovies.network.NetworkUtils;
import com.example.federico.popularmovies.network.RetrofitClient;
import com.example.federico.popularmovies.utils.Constants;
import com.example.federico.popularmovies.utils.Utils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String DETAIL_MOVIE_INTENT = "movieClickedIntent";
    private static final String TAG = "Main Activity";
    private static final String SAVED_INSTANCE_SPINNER_SELECTION = "keySavedInstanceSpinner";
    private static final String SCROLL_POSITION = "keyScrollPosition";
    private static String SORT_METHOD = "";
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    CustomAdapter customAdapter;
    Parcelable gridviewState;
    Parcelable stateR;
    private GridView gridview;
    private List<MovieEntry> favoriteMovies;
    private Integer itemSelected = 0;


    /*-------------------------------------- OnCreate ----------------------------------------------***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        gridview = findViewById(R.id.gridView);

        //method to retrieve favorite movies from DB. Top Rated and Popular Movies are retrieved from API when
        // sort method is selected in the menu spinner
        loadFavoriteMovies();
        // requestToGetMovies(SORT_METHOD);


    }

    /*-------------------------------------- Get Movies ----------------------------------------------***/

    private void requestToGetMovies(CharSequence sortByMethod) {

        if (sortByMethod.equals(Constants.SORT_BY_FAVORITES)) {
            createGridview(favoriteMovies);

        } else {

            if (!NetworkUtils.isNetworkConnected(MainActivity.this)) {
                Toast.makeText(MainActivity.this, R.string.check_internet, Toast.LENGTH_LONG).show();

            } else {

                progressBar.setVisibility(View.VISIBLE);
                APIInterface mService = RetrofitClient.getClient(getApplicationContext()).create(APIInterface.class);
                Call<Movies<MovieEntry>> callGetMovies = mService.getMovies(NetworkUtils.OBJECT, Utils.getSortParameterURL((String) sortByMethod), BuildConfig.MY_MOVIE_DB_API_KEY);
                callGetMovies.enqueue(new Callback<Movies<MovieEntry>>() {
                    @Override
                    public void onResponse(Call<Movies<MovieEntry>> call, Response<Movies<MovieEntry>> response) {

                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d(TAG, call.request().toString());

                        if (response.isSuccessful()) {

                            try {
                                Movies<MovieEntry> movies = response.body();
                                assert movies != null;
                                List<MovieEntry> moviesList = movies.getResults();
                                createGridview(moviesList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {

                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    Log.i(TAG, Objects.requireNonNull(response.errorBody()).string());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<Movies<MovieEntry>> call, Throwable t) {

                        progressBar.setVisibility(View.INVISIBLE);

                        Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_LONG).show();

                        Log.e(TAG, t.getMessage());

                    }
                });
            }

        }
    }

    /*-------------------------------------- Load Favorite Movies ----------------------------------------------***/

    private void loadFavoriteMovies() {

        final MainActivityViewModel viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {

                Log.d(TAG, "Updating list of movies from LiveData in ViewModel");
                favoriteMovies = movieEntries;

                // this was the best solution I could come up with to notify the adapter that data the data
                // had changed without losing the possibility to access it without internet connection
                if (customAdapter != null && SORT_METHOD.equals(Constants.SORT_BY_FAVORITES)) {
                    customAdapter.refresh(favoriteMovies);
                }

            }
        });

    }

    /*-------------------------------------- Create GridView ----------------------------------------------***/

    private void createGridview(final List<MovieEntry> moviesList) {

        customAdapter = new CustomAdapter(MainActivity.this, moviesList);
        gridview.setAdapter(customAdapter);
        if (gridviewState != null) {
            gridview.onRestoreInstanceState(gridviewState);
        }

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Context context = MainActivity.this;
                Class destinationClass = DetailActivity.class;
                Intent detailIntent = new Intent(context, destinationClass);
                Gson gsonMovie = new Gson();
                MovieEntry movieClicked = moviesList.get(i);
                detailIntent.putExtra(DETAIL_MOVIE_INTENT, gsonMovie.toJson(movieClicked));
                startActivity(detailIntent);

            }
        });
    }

    // source code copied from https://www.viralandroid.com/2016/03/how-to-add-spinner-dropdown-list-to-android-actionbar-toolbar.html
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.spinner);

        //noinspection deprecation - could not find a replacement for this deprecated code
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        final List<CharSequence> itemsSpinnerSort = Utils.getSortOptions();
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsSpinnerSort);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (itemSelected != null) {
            //save the position of the item selected for onSaveInstanceState()
            spinner.setSelection(itemSelected);
            if (stateR != null) {
                gridview.onRestoreInstanceState(stateR);
            }

        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                itemSelected = i;
                SORT_METHOD = itemsSpinnerSort.get(i).toString();
                requestToGetMovies(SORT_METHOD);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return true;
    }


    /*-------------------------------------- OnSave and Restore Instance State ----------------------------------------------***/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // saves the id of the position of the sorting method selected
        outState.putInt(SAVED_INSTANCE_SPINNER_SELECTION, itemSelected);

        //saves scroll position
        gridviewState = gridview.onSaveInstanceState();
        outState.putParcelable(SCROLL_POSITION, gridviewState);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        itemSelected = savedInstanceState.getInt(SAVED_INSTANCE_SPINNER_SELECTION);
        gridviewState = savedInstanceState.getParcelable(SCROLL_POSITION);

    }

}
