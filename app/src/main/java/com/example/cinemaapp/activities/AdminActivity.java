package com.example.cinemaapp.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cinemaapp.R;
import com.example.cinemaapp.database.DatabaseHelper;
import com.example.cinemaapp.models.Movie;
import com.example.cinemaapp.models.Session;

import java.util.Calendar;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvTotalTickets;
    private TextView tvTotalRevenue;
    private LinearLayout llMoviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Исправление шторки
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(true);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Панель администратора");
        }

        dbHelper = new DatabaseHelper(this);
        tvTotalTickets = findViewById(R.id.tvTotalTickets);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        llMoviesList = findViewById(R.id.llMoviesList);

        Button btnAddMovie = findViewById(R.id.btnAddMovie);
        Button btnAddSession = findViewById(R.id.btnAddSession);

        btnAddMovie.setOnClickListener(v -> showAddMovieDialog());
        btnAddSession.setOnClickListener(v -> showAddSessionDialog());

        refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        loadStatistics();
        loadMoviesList();
    }

    private void loadStatistics() {
        int totalTickets = dbHelper.getTotalTicketsSold();
        double totalRevenue = dbHelper.getTotalRevenue();
        tvTotalTickets.setText("Продано билетов: " + totalTickets);
        tvTotalRevenue.setText("Выручка: " + String.format("%.0f", totalRevenue) + " ₸");
    }

    private void loadMoviesList() {
        llMoviesList.removeAllViews();
        List<Movie> movies = dbHelper.getAllMovies();

        if (movies.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Нет добавленных фильмов");
            tvEmpty.setTextColor(0xFFAAAAAA);
            tvEmpty.setPadding(0, 16, 0, 16);
            llMoviesList.addView(tvEmpty);
            return;
        }

        for (Movie movie : movies) {
            View movieView = LayoutInflater.from(this)
                    .inflate(R.layout.item_admin_movie, llMoviesList, false);

            TextView tvTitle = movieView.findViewById(R.id.tvMovieTitleAdmin);
            TextView tvDuration = movieView.findViewById(R.id.tvMovieDurationAdmin);
            Button btnDelete = movieView.findViewById(R.id.btnDeleteMovie);
            Button btnSessions = movieView.findViewById(R.id.btnViewSessions);

            tvTitle.setText(movie.getTitle());
            tvDuration.setText(movie.getDuration() + " мин");

            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Удалить фильм?")
                        .setMessage("Фильм \"" + movie.getTitle() + "\" и все его сеансы будут удалены.")
                        .setPositiveButton("Удалить", (dialog, which) -> {
                            boolean success = dbHelper.deleteMovie(movie.getId());
                            if (success) {
                                Toast.makeText(this, "Фильм удалён", Toast.LENGTH_SHORT).show();
                                refreshData();
                            } else {
                                Toast.makeText(this, "Ошибка при удалении", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            });

            btnSessions.setOnClickListener(v -> showMovieSessions(movie));
            llMoviesList.addView(movieView);
        }
    }

    private void showAddMovieDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_movie, null);

        EditText etTitle = dialogView.findViewById(R.id.etMovieTitle);
        EditText etDescription = dialogView.findViewById(R.id.etMovieDescription);
        EditText etDuration = dialogView.findViewById(R.id.etMovieDuration);
        EditText etPoster = dialogView.findViewById(R.id.etMoviePoster);

        new AlertDialog.Builder(this)
                .setTitle("Добавить фильм")
                .setView(dialogView)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String desc = etDescription.getText().toString().trim();
                    String durationStr = etDuration.getText().toString().trim();
                    String poster = etPoster.getText().toString().trim();

                    if (title.isEmpty() || durationStr.isEmpty()) {
                        Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int duration = Integer.parseInt(durationStr);
                    if (poster.isEmpty()) poster = "ic_movie_placeholder";

                    long movieId = dbHelper.addMovie(title, desc, duration, poster);
                    if (movieId != -1) {
                        Toast.makeText(this, "Фильм добавлен!", Toast.LENGTH_SHORT).show();
                        refreshData();
                    } else {
                        Toast.makeText(this, "Ошибка при добавлении", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showAddSessionDialog() {
        List<Movie> movies = dbHelper.getAllMovies();
        if (movies.isEmpty()) {
            Toast.makeText(this, "Сначала добавьте хотя бы один фильм", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_session, null);

        Spinner spinnerMovies = dialogView.findViewById(R.id.spinnerMovies);
        EditText etDate = dialogView.findViewById(R.id.etSessionDate);
        EditText etTime = dialogView.findViewById(R.id.etSessionTime);
        EditText etPrice = dialogView.findViewById(R.id.etSessionPrice);

        String[] movieTitles = new String[movies.size()];
        for (int i = 0; i < movies.size(); i++) {
            movieTitles[i] = movies.get(i).getTitle();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, movieTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMovies.setAdapter(adapter);

        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                etDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        etTime.setFocusable(false);
        etTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(this, (view, hour, minute) -> {
                etTime.setText(String.format("%02d:%02d", hour, minute));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
        });

        new AlertDialog.Builder(this)
                .setTitle("Добавить сеанс")
                .setView(dialogView)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    int selectedIndex = spinnerMovies.getSelectedItemPosition();
                    Movie selectedMovie = movies.get(selectedIndex);
                    String date = etDate.getText().toString().trim();
                    String time = etTime.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();

                    if (date.isEmpty() || time.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price = Double.parseDouble(priceStr);
                    long sessionId = dbHelper.addSession(selectedMovie.getId(), date, time, price);
                    if (sessionId != -1) {
                        Toast.makeText(this, "Сеанс добавлен!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Ошибка при добавлении сеанса", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showMovieSessions(Movie movie) {
        List<Session> sessions = dbHelper.getSessionsByMovieId(movie.getId());
        StringBuilder sb = new StringBuilder();
        if (sessions.isEmpty()) {
            sb.append("Нет сеансов");
        } else {
            for (Session s : sessions) {
                sb.append("📅 ").append(s.getDate())
                        .append(" ⏰ ").append(s.getTime())
                        .append(" — ").append((int) s.getPrice()).append(" ₸\n");
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("Сеансы: " + movie.getTitle())
                .setMessage(sb.toString())
                .setPositiveButton("OK", null)
                .show();
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