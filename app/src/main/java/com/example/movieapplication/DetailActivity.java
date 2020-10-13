package com.example.movieapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.movieapplication.adapter.TrailerAdapter;
import com.example.movieapplication.api.Client;
import com.example.movieapplication.api.Service;
import com.example.movieapplication.model.Trailer;
import com.example.movieapplication.model.TrailerResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    TextView toolbar_movie_title;
    TextView nameOfMovie, overView, userRating, releaseDate, popularity, isAdult;
    ImageView movieThumbnail;

    private RecyclerView recyclerView;
    private TrailerAdapter adapter;
    private List<Trailer> trailerList;
    private final AppCompatActivity activity = DetailActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initToolbar();

        toolbar_movie_title = findViewById(R.id.movie_detail_toolbar_title);
        movieThumbnail = findViewById(R.id.movie_detail_image);
        nameOfMovie = findViewById(R.id.movie_detail_title);
        userRating = findViewById(R.id.movie_detail_rating);
        releaseDate = findViewById(R.id.movie_detail_releasedate);
        overView = findViewById(R.id.movie_detail_description);
        popularity = findViewById(R.id.movie_detail_popularity);
        isAdult = findViewById(R.id.movie_detail_adult);

        Intent intentThatStartThisAct = getIntent();

        if (intentThatStartThisAct.hasExtra("original_title")) {
            String thumbnail = getIntent().getExtras().getString("poster_path");
            String movieName = getIntent().getExtras().getString("original_title");
            String synopsis = getIntent().getExtras().getString("overview");
            String rating = getIntent().getExtras().getString("vote_average");
            String dateOfRelease = getIntent().getExtras().getString("release_date");
            String str_popularity = getIntent().getExtras().getString("popularity");
            String str_adult = getIntent().getExtras().getString("adult");

            Glide.with(this)
                    .load(thumbnail)
                    .placeholder(R.drawable.image_12)
                    .into(movieThumbnail);

            toolbar_movie_title.setText(movieName);
            nameOfMovie.setText(movieName);
            userRating.setText(rating);
            releaseDate.setText(dateOfRelease);
            overView.setText(synopsis);
            popularity.setText(str_popularity);
            isAdult.setText(str_adult);

        } else {
            Toast.makeText(this, "NO API DATA", Toast.LENGTH_SHORT).show();
        }

        initTrailerViews();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.detailToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void initTrailerViews() {
        trailerList = new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerList);

        recyclerView = findViewById(R.id.trailerListRecycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        loadJSON();
    }

    private void loadJSON() {
        int movie_id = getIntent().getExtras().getInt("id");
        try {
            if (BuildConfig.THE_MOVIE_DB_API_KEY.isEmpty()) {
                Toast.makeText(this, "Please Obtain your API Key from themoviesdb.org", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<TrailerResponse> call = apiService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API_KEY);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    List<Trailer> trailer = response.body().getResults();
                    recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Log.e("Error",  t.getMessage());
                    Toast.makeText(DetailActivity.this, "Error Fetching Trailer Data", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("Error", e.getMessage() );
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}