    package com.example.cinemaapp.activities;
    
    import android.content.SharedPreferences;
    import android.graphics.Color;
    import android.os.Bundle;
    import android.view.Gravity;
    import android.widget.Button;
    import android.widget.GridLayout;
    import android.widget.TextView;
    import android.widget.Toast;
    
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.Toolbar;
    
    import com.example.cinemaapp.R;
    import com.example.cinemaapp.database.DatabaseHelper;
    import com.example.cinemaapp.models.Seat;
    
    import java.util.ArrayList;
    import java.util.List;
    
    public class SeatSelectionActivity extends AppCompatActivity {
    
        private static final int COLOR_FREE     = Color.parseColor("#4CAF50");
        private static final int COLOR_BOOKED   = Color.parseColor("#F44336");
        private static final int COLOR_SELECTED = Color.parseColor("#FFC107");
    
        private static final int ROWS = 5;
        private static final int SEATS_PER_ROW = 8;
    
        private GridLayout gridSeats;
        private TextView tvSelectedInfo;
        private TextView tvTotalPrice;
        private Button btnConfirm;
    
        private DatabaseHelper dbHelper;
        private List<Seat> allSeats;
        private List<Seat> selectedSeats;
    
        private int sessionId;
        private String movieTitle;
        private double seatPrice;
        private int currentUserId;
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_seat_selection);
    
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
    
            sessionId = getIntent().getIntExtra("session_id", -1);
            movieTitle = getIntent().getStringExtra("movie_title");
            seatPrice = getIntent().getDoubleExtra("session_price", 0);
    
            if (sessionId == -1) {
                Toast.makeText(this, "Ошибка: сеанс не найден", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
    
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(movieTitle != null ? movieTitle : "Выбор мест");
            }
    
            SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
            currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);
    
            dbHelper = new DatabaseHelper(this);
            selectedSeats = new ArrayList<>();
    
            gridSeats = findViewById(R.id.gridSeats);
            tvSelectedInfo = findViewById(R.id.tvSelectedInfo);
            tvTotalPrice = findViewById(R.id.tvTotalPrice);
            btnConfirm = findViewById(R.id.btnConfirm);
    
            loadSeats();
            btnConfirm.setOnClickListener(v -> confirmBooking());
            updateInfoPanel();
        }
    
        private void loadSeats() {
            allSeats = dbHelper.getSeatsBySessionId(sessionId);
            gridSeats.removeAllViews();
            gridSeats.setColumnCount(SEATS_PER_ROW + 1);
            gridSeats.setRowCount(ROWS + 1);
    
            addHeaderCell("");
            for (int col = 1; col <= SEATS_PER_ROW; col++) {
                addHeaderCell(String.valueOf(col));
            }
    
            for (int row = 1; row <= ROWS; row++) {
                addHeaderCell("Р" + row);
                for (int seatNum = 1; seatNum <= SEATS_PER_ROW; seatNum++) {
                    final Seat seat = findSeat(row, seatNum);
                    if (seat != null) {
                        Button btn = createSeatButton(seat);
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = dpToPx(36);
                        params.height = dpToPx(36);
                        params.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
                        params.rowSpec = GridLayout.spec(row, 1, GridLayout.CENTER);
                        params.columnSpec = GridLayout.spec(seatNum, 1, GridLayout.CENTER);
                        btn.setLayoutParams(params);
                        gridSeats.addView(btn);
                    }
                }
            }
        }
    
        private void addHeaderCell(String text) {
            TextView tv = new TextView(this);
            tv.setText(text);
            tv.setTextColor(Color.parseColor("#AAAAAA"));
            tv.setTextSize(10f);
            tv.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = dpToPx(36);
            params.height = dpToPx(24);
            params.setMargins(dpToPx(2), 0, dpToPx(2), dpToPx(4));
            tv.setLayoutParams(params);
            gridSeats.addView(tv);
        }
    
        private Button createSeatButton(Seat seat) {
            Button btn = new Button(this);
            btn.setText("");
            btn.setPadding(0, 0, 0, 0);
    
            if (seat.isBooked()) {
                btn.setBackgroundColor(COLOR_BOOKED);
                btn.setEnabled(false);
            } else {
                btn.setBackgroundColor(COLOR_FREE);
                btn.setEnabled(true);
            }
    
            btn.setOnClickListener(v -> {
                if (seat.isBooked()) return;
                if (seat.isSelected()) {
                    seat.setSelected(false);
                    btn.setBackgroundColor(COLOR_FREE);
                    selectedSeats.remove(seat);
                } else {
                    seat.setSelected(true);
                    btn.setBackgroundColor(COLOR_SELECTED);
                    selectedSeats.add(seat);
                }
                updateInfoPanel();
            });
    
            return btn;
        }
    
        private Seat findSeat(int row, int seatNumber) {
            for (Seat seat : allSeats) {
                if (seat.getRowNumber() == row && seat.getSeatNumber() == seatNumber) {
                    return seat;
                }
            }
            return null;
        }
    
        private void updateInfoPanel() {
            int count = selectedSeats.size();
            double total = count * seatPrice;
            tvSelectedInfo.setText("Выбрано мест: " + count);
            tvTotalPrice.setText("Итого: " + (int) total + " T");
            btnConfirm.setEnabled(count > 0);
        }
    
        private void confirmBooking() {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Выберите хотя бы одно место", Toast.LENGTH_SHORT).show();
                return;
            }
    
            int count = selectedSeats.size();
            double total = count * seatPrice;
    
            new AlertDialog.Builder(this)
                    .setTitle("Подтверждение бронирования")
                    .setMessage("Вы выбрали " + count + " место(а).\nИтого: " + (int) total + " T\n\nПодтвердить?")
                    .setPositiveButton("Да, купить", (dialog, which) -> processBooking())
                    .setNegativeButton("Отмена", null)
                    .show();
        }
    
        private void processBooking() {
            boolean allSuccess = true;
            for (Seat seat : selectedSeats) {
                long ticketId = dbHelper.createTicket(currentUserId, sessionId, seat.getId(), seatPrice);
                if (ticketId == -1) allSuccess = false;
            }
    
            if (allSuccess) {
                Toast.makeText(this, "Бронирование успешно! Куплено " + selectedSeats.size() + " билет(а).", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Ошибка при бронировании.", Toast.LENGTH_LONG).show();
            }
        }
    
        private int dpToPx(int dp) {
            float density = getResources().getDisplayMetrics().density;
            return Math.round(dp * density);
        }
    
        @Override
        public boolean onSupportNavigateUp() {
            onBackPressed();
            return true;
        }
    }