package com.example.cinemaapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinemaapp.R;
import com.example.cinemaapp.adapters.MovieAdapter;
import com.example.cinemaapp.adapters.TrendingMovieAdapter;
import com.example.cinemaapp.database.DatabaseHelper;
import com.example.cinemaapp.models.Movie;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMovies;
    private RecyclerView recyclerViewTrending;
    private MovieAdapter movieAdapter;
    private TrendingMovieAdapter trendingAdapter;
    private DatabaseHelper dbHelper;
    private TextView tvWelcome;
    private TextView tvAvatarCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(true);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("🎬 CINEMA");
        }

        dbHelper = new DatabaseHelper(this);

        tvWelcome      = findViewById(R.id.tvWelcome);
        tvAvatarCircle = findViewById(R.id.tvAvatarCircle);

        String username = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                .getString(LoginActivity.KEY_USERNAME, "");

        tvWelcome.setText("Привет, " + username + "!");
        if (!username.isEmpty()) {
            tvAvatarCircle.setText(String.valueOf(username.charAt(0)).toUpperCase());
        }

        recyclerViewTrending = findViewById(R.id.recyclerViewTrending);
        recyclerViewTrending.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerViewMovies = findViewById(R.id.recyclerViewMovies);
        recyclerViewMovies.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewMovies.setNestedScrollingEnabled(false);

        loadMovies();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovies();
    }

    private void loadMovies() {
        List<Movie> movies = dbHelper.getAllMovies();
        if (movieAdapter == null) {
            movieAdapter = new MovieAdapter(movies, movie -> {
                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                intent.putExtra("movie_id", movie.getId());
                startActivity(intent);
            });
            recyclerViewMovies.setAdapter(movieAdapter);
        } else {
            movieAdapter.updateMovies(movies);
        }

        List<Movie> trending = dbHelper.getTrendingMovies(5);
        if (trendingAdapter == null) {
            trendingAdapter = new TrendingMovieAdapter(trending, movie -> {
                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                intent.putExtra("movie_id", movie.getId());
                startActivity(intent);
            });
            recyclerViewTrending.setAdapter(trendingAdapter);
        } else {
            trendingAdapter.updateMovies(trending);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        } else if (item.getItemId() == R.id.action_my_tickets) {
            startActivity(new Intent(this, MyTicketsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}