package com.example.movieapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.movieapplication.adapter.MoviesAdapter;
import com.example.movieapplication.api.Client;
import com.example.movieapplication.api.Service;
import com.example.movieapplication.model.Movie;
import com.example.movieapplication.model.MoviesResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity  {

    public static final String TAG = "Activity";

    private RecyclerView movieListRecycle;
    private MoviesAdapter adapter;
    private List<Movie> movieList;

    private AppCompatActivity activity = MainActivity.this;
    private SharedPreferences shared_sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shared_sort = getSharedPreferences("MyPref", 0);

        initToolbar();
        initComponents();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.movieListToolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.grey_80));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Movie List");
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void initComponents() {
        movieListRecycle = findViewById(R.id.movieListRecycle);
        movieList = new ArrayList<>();
        adapter = new MoviesAdapter(this, movieList);

        movieListRecycle.setLayoutManager(new LinearLayoutManager(this));
        movieListRecycle.setHasFixedSize(true);
        movieListRecycle.setItemAnimator(new DefaultItemAnimator());
        movieListRecycle.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        boolean isPopular = shared_sort.getBoolean("isPopular", true);

        if (isPopular) {
            loadJSONPopular();
        } else {
            loadJSONTopRated();
        }
    }

    private void loadJSONPopular() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_KEY.isEmpty()) {
                Toast.makeText(activity, "Please obtain API Key firsty from Website", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_KEY);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    Collections.sort(movies, Movie.BY_NAME_ALPHABETICAL);
                    movieListRecycle.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    movieListRecycle.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.e("Error", "onFailure: " + t.getMessage());
                    Toast.makeText(MainActivity.this, "Error in Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            Log.e(TAG, "loadJSONPopular: " + e.getMessage() );
        }
    }

    private void loadJSONTopRated() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_KEY.isEmpty()) {
                Toast.makeText(this, "Please Obtain API Key firstly from Website...", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<MoviesResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_KEY);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    movieListRecycle.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    movieListRecycle.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.e("Error", "onFailure: " + t.getMessage());
                    Toast.makeText(MainActivity.this, "Error in Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("Exception", "loadJSON: " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = shared_sort.edit();

        switch (item.getItemId()) {
            case R.id.sortByRating:
                editor.putBoolean("isPopular", false);
                editor.apply();
                initComponents();
                return true;

            case R.id.sortByPopular:
                editor.putBoolean("isPopular", true);
                editor.apply();
                initComponents();
                return true;
            case R.id.refreshList:
                initComponents();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}