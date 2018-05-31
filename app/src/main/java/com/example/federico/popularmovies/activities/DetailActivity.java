package com.example.federico.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.federico.popularmovies.R;
import com.example.federico.popularmovies.model.Result;
import com.example.federico.popularmovies.network.NetworkUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    private Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        View contentView = findViewById(R.id.content_view);

        TextView tvPlotSynopsis = contentView.findViewById(R.id.tvPlotSynopsis);

        TextView tvReleaseDate = contentView.findViewById(R.id.tvReleaseDate);

        TextView tvVoteAverage = contentView.findViewById(R.id.tvVoteAverage);

        ImageView imagePoster = contentView.findViewById(R.id.imagePoster);

        Intent intent = getIntent();

        if (intent.hasExtra(MainActivity.DETAIL_MOVIE_INTENT)) {

            String movieJson = intent.getStringExtra(MainActivity.DETAIL_MOVIE_INTENT);

            Gson movieGson = new Gson();

            result = movieGson.fromJson(movieJson, Result.class);

        }

        if (result != null) {

            String imagePath = result.getPosterPath();

            String imageURL = NetworkUtils.IMAGES_URL + NetworkUtils.IMAGES_SIZE + imagePath;

            Context context = DetailActivity.this;

            int width = context.getResources().getDisplayMetrics().widthPixels;

            Picasso.get().load(imageURL)
                    .centerCrop()
                    .resize(width / 3, width / 2)
                    .error(R.drawable.ic_baseline_error_24px)
                    .into(imagePoster);

            getSupportActionBar().setTitle(result.getTitle());

            tvPlotSynopsis.setText(result.getOverview());

            tvReleaseDate.setText(result.getReleaseDate());

            tvVoteAverage.setText(String.valueOf(result.getVoteAverage()));

        }
    }

}
