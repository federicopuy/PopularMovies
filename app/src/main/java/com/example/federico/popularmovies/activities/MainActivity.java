package com.example.federico.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.example.federico.popularmovies.model.Movies;
import com.example.federico.popularmovies.model.Result;
import com.example.federico.popularmovies.network.APIInterface;
import com.example.federico.popularmovies.network.NetworkUtils;
import com.example.federico.popularmovies.network.RetrofitClient;
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

    private GridView gridview;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        gridview = findViewById(R.id.gridView);

    }

    private void requestToGetMovies(CharSequence sortByMethod) {

        progressBar.setVisibility(View.VISIBLE);

        APIInterface mService = RetrofitClient.getClient(getApplicationContext()).create(APIInterface.class);

        Call<Movies> callGetMovies = mService.getMovies(NetworkUtils.OBJECT, Utils.getSortParameterURL((String) sortByMethod), BuildConfig.MY_MOVIE_DB_API_KEY);

        callGetMovies.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {

                progressBar.setVisibility(View.INVISIBLE);

                Log.d(TAG, call.request().toString());

                if (response.isSuccessful()) {

                    try {

                        Movies movies = response.body();

                        assert movies != null;
                        List<Result> moviesList = movies.getResults();

                        createGridview(moviesList);

                    }   catch (Exception e){
                        e.printStackTrace();
                    }

                } else {

                    try {
                        Log.i(TAG, Objects.requireNonNull(response.errorBody()).string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_LONG).show();

                }


            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {

                progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_LONG).show();

                Log.e(TAG, t.getMessage());

            }
        });

    }

    private void createGridview(final List<Result> moviesList) {

        CustomAdapter customAdapter = new CustomAdapter(MainActivity.this, moviesList);

        gridview.setAdapter(customAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Context context = MainActivity.this;

                Class destinationClass = DetailActivity.class;

                Intent detailIntent = new Intent(context, destinationClass);

                Gson gsonMovie = new Gson();

                Result movieClicked = moviesList.get(i);

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


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                if (!isNetworkConnected()){

                    Toast.makeText(MainActivity.this, R.string.check_internet, Toast.LENGTH_LONG).show();

                } else {

                    requestToGetMovies(itemsSpinnerSort.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }



}
