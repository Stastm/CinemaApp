package com.example.cinemaapp.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cinemaapp.R;
import com.example.cinemaapp.database.DatabaseHelper;
import com.example.cinemaapp.models.Movie;
import com.example.cinemaapp.models.Seat;
import com.example.cinemaapp.models.Session;
import com.example.cinemaapp.models.Ticket;

import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private LinearLayout llTickets;
    private DatabaseHelper dbHelper;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Мои билеты");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        currentUserId = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                .getInt(LoginActivity.KEY_USER_ID, -1);

        llTickets = findViewById(R.id.llTickets);
        loadTickets();
    }

    private void loadTickets() {
        List<Ticket> tickets = dbHelper.getTicketsByUserId(currentUserId);
        llTickets.removeAllViews();

        if (tickets.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("У вас нет купленных билетов");
            tvEmpty.setTextColor(0xFFAAAAAA);
            tvEmpty.setTextSize(16f);
            tvEmpty.setPadding(32, 32, 32, 32);
            llTickets.addView(tvEmpty);
            return;
        }

        for (Ticket ticket : tickets) {
            View ticketView = LayoutInflater.from(this)
                    .inflate(R.layout.item_ticket, llTickets, false);

            TextView tvMovie = ticketView.findViewById(R.id.tvTicketMovie);
            TextView tvSession = ticketView.findViewById(R.id.tvTicketSession);
            TextView tvSeat = ticketView.findViewById(R.id.tvTicketSeat);
            TextView tvPrice = ticketView.findViewById(R.id.tvTicketPrice);

            Session session = dbHelper.getSessionById(ticket.getSessionId());
            Seat seat = dbHelper.getSeatById(ticket.getSeatId());

            if (session != null) {
                Movie movie = dbHelper.getMovieById(session.getMovieId());
                if (movie != null) {
                    tvMovie.setText("🎬 " + movie.getTitle());
                }
                tvSession.setText("📅 " + session.getDate() + " в " + session.getTime());
            }

            if (seat != null) {
                tvSeat.setText("💺 Ряд " + seat.getRowNumber() + ", Место " + seat.getSeatNumber());
            }

            tvPrice.setText("💰 " + (int) ticket.getTotalPrice() + " ₸");
            llTickets.addView(ticketView);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}