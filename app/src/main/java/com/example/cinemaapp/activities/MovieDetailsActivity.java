package com.example.cinemaapp.activities;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cinemaapp.R;
import com.example.cinemaapp.database.DatabaseHelper;
import com.example.cinemaapp.models.Movie;
import com.example.cinemaapp.models.Review;
import com.example.cinemaapp.models.Session;

import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView ivPoster;
    private TextView tvTitle, tvDescription, tvDuration, tvRating;
    private LinearLayout llSessions, llReviews;
    private Button btnWriteReview;

    private DatabaseHelper dbHelper;
    private Movie currentMovie;
    private int currentUserId;
    private float selectedRating = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ivPoster = findViewById(R.id.ivPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvDuration = findViewById(R.id.tvDuration);
        tvRating = findViewById(R.id.tvRating);
        llSessions = findViewById(R.id.llSessions);
        llReviews = findViewById(R.id.llReviews);
        btnWriteReview = findViewById(R.id.btnWriteReview);

        dbHelper = new DatabaseHelper(this);
        currentUserId = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                .getInt(LoginActivity.KEY_USER_ID, -1);

        int movieId = getIntent().getIntExtra("movie_id", -1);
        if (movieId == -1) {
            Toast.makeText(this, "Ошибка: фильм не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadMovieDetails(movieId);

        btnWriteReview.setOnClickListener(v -> showAddReviewDialog());
    }

    private void loadMovieDetails(int movieId) {
        currentMovie = dbHelper.getMovieById(movieId);
        if (currentMovie == null) {
            finish();
            return;
        }

        tvTitle.setText(currentMovie.getTitle());
        tvDescription.setText(currentMovie.getDescription());
        tvDuration.setText("Длительность: " + currentMovie.getDuration() + " мин");

        float avgRating = dbHelper.getAverageRating(movieId);
        if (avgRating > 0) {
            tvRating.setText(String.format("★ %.1f", avgRating));
            tvRating.setVisibility(View.VISIBLE);
        } else {
            tvRating.setText("★ —");
            tvRating.setVisibility(View.VISIBLE);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(currentMovie.getTitle());
        }

        int resId = getResources().getIdentifier(
                currentMovie.getPoster(), "drawable", getPackageName());
        if (resId != 0) {
            ivPoster.setImageResource(resId);
        } else {
            ivPoster.setImageResource(R.drawable.ic_movie_placeholder);
        }

        loadSessions(movieId);
        loadReviews(movieId);
    }

    private void loadSessions(int movieId) {
        List<Session> sessions = dbHelper.getSessionsByMovieId(movieId);
        llSessions.removeAllViews();

        if (sessions.isEmpty()) {
            TextView tvNoSessions = new TextView(this);
            tvNoSessions.setText("Нет доступных сеансов");
            tvNoSessions.setTextColor(0xFFAAAAAA);
            llSessions.addView(tvNoSessions);
            return;
        }

        for (Session session : sessions) {
            View sessionView = getLayoutInflater().inflate(R.layout.item_session, llSessions, false);
            TextView tvSessionDateTime = sessionView.findViewById(R.id.tvSessionDateTime);
            TextView tvSessionPrice = sessionView.findViewById(R.id.tvSessionPrice);
            Button btnBuyTickets = sessionView.findViewById(R.id.btnBuyTickets);

            tvSessionDateTime.setText(session.getDate() + " в " + session.getTime());
            tvSessionPrice.setText("Цена: " + (int) session.getPrice() + " ₸");

            btnBuyTickets.setOnClickListener(v -> {
                Intent intent = new Intent(MovieDetailsActivity.this, SeatSelectionActivity.class);
                intent.putExtra("session_id", session.getId());
                intent.putExtra("movie_title", currentMovie.getTitle());
                intent.putExtra("session_price", session.getPrice());
                startActivity(intent);
            });

            llSessions.addView(sessionView);
        }
    }

    private void loadReviews(int movieId) {
        List<Review> reviews = dbHelper.getReviewsByMovieId(movieId);
        llReviews.removeAllViews();

        if (reviews.isEmpty()) {
            TextView tvNoReviews = new TextView(this);
            tvNoReviews.setText("Пока нет отзывов. Будьте первым!");
            tvNoReviews.setTextColor(0xFF555555);
            tvNoReviews.setTextSize(13f);
            tvNoReviews.setPadding(0, 8, 0, 8);
            llReviews.addView(tvNoReviews);
            return;
        }

        for (Review review : reviews) {
            View reviewView = getLayoutInflater().inflate(R.layout.item_review, llReviews, false);

            TextView tvAvatar = reviewView.findViewById(R.id.tvReviewAvatar);
            TextView tvUsername = reviewView.findViewById(R.id.tvReviewUsername);
            TextView tvDate = reviewView.findViewById(R.id.tvReviewDate);
            TextView tvRatingItem = reviewView.findViewById(R.id.tvReviewRating);
            TextView tvComment = reviewView.findViewById(R.id.tvReviewComment);

            String username = dbHelper.getUsernameById(review.getUserId());
            tvUsername.setText(username);
            tvAvatar.setText(String.valueOf(username.charAt(0)).toUpperCase());
            tvDate.setText(review.getDate());
            tvRatingItem.setText(String.format("★ %.0f", review.getRating()));
            tvComment.setText(review.getComment());

            llReviews.addView(reviewView);
        }
    }

    private void showAddReviewDialog() {
        if (currentUserId == -1) {
            Toast.makeText(this, "Войдите чтобы оставить отзыв", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.hasUserReviewed(currentUserId, currentMovie.getId())) {
            Toast.makeText(this, "Вы уже оставили отзыв на этот фильм", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_review, null);

        Button btn1 = dialogView.findViewById(R.id.btn1Star);
        Button btn2 = dialogView.findViewById(R.id.btn2Star);
        Button btn3 = dialogView.findViewById(R.id.btn3Star);
        Button btn4 = dialogView.findViewById(R.id.btn4Star);
        Button btn5 = dialogView.findViewById(R.id.btn5Star);
        EditText etComment = dialogView.findViewById(R.id.etReviewComment);

        selectedRating = 0f;
        Button[] starBtns = {btn1, btn2, btn3, btn4, btn5};

        for (int i = 0; i < starBtns.length; i++) {
            final float rating = i + 1;
            starBtns[i].setOnClickListener(v -> {
                selectedRating = rating;
                for (Button b : starBtns) {
                    b.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(0xFF2A2A2A));
                    b.setTextColor(0xFFAAAAAA);
                }
                for (int j = 0; j < (int) rating; j++) {
                    starBtns[j].setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(0xFFE50914));
                    starBtns[j].setTextColor(0xFFFFFFFF);
                }
            });
        }

        new AlertDialog.Builder(this)
                .setTitle("Написать отзыв")
                .setView(dialogView)
                .setPositiveButton("Отправить", (dialog, which) -> {
                    if (selectedRating == 0) {
                        Toast.makeText(this, "Выберите оценку!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String comment = etComment.getText().toString().trim();
                    if (comment.isEmpty()) {
                        Toast.makeText(this, "Напишите отзыв!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    long result = dbHelper.addReview(currentUserId, currentMovie.getId(), selectedRating, comment);
                    if (result != -1) {
                        Toast.makeText(this, "Отзыв добавлен!", Toast.LENGTH_SHORT).show();
                        loadMovieDetails(currentMovie.getId());
                    } else {
                        Toast.makeText(this, "Ошибка при добавлении отзыва", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}