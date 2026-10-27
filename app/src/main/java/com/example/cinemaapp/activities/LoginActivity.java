package com.example.cinemaapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinemaapp.R;
import com.example.cinemaapp.database.DatabaseHelper;
import com.example.cinemaapp.models.User;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "CinemaPrefs";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_ROLE = "user_role";
    public static final String KEY_USERNAME = "username";

    private static final String TELEGRAM_USERNAME = "stastome";

    private EditText etUsername, etPassword;
    private EditText etRegUsername, etRegPassword, etRegPasswordConfirm;
    private Button btnLogin, btnRegister;
    private Button btnTabLogin, btnTabRegister;
    private Button btnTelegram;
    private LinearLayout layoutLogin, layoutRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(true);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }

        dbHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegPasswordConfirm = findViewById(R.id.etRegPasswordConfirm);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnTabLogin = findViewById(R.id.btnTabLogin);
        btnTabRegister = findViewById(R.id.btnTabRegister);
        btnTelegram = findViewById(R.id.btnTelegram);

        layoutLogin = findViewById(R.id.layoutLogin);
        layoutRegister = findViewById(R.id.layoutRegister);

        checkExistingSession();

        btnTabLogin.setOnClickListener(v -> showLoginTab());
        btnTabRegister.setOnClickListener(v -> showRegisterTab());
        btnLogin.setOnClickListener(v -> performLogin());
        btnRegister.setOnClickListener(v -> performRegister());
        btnTelegram.setOnClickListener(v -> openTelegram());
    }

    private void showLoginTab() {
        layoutLogin.setVisibility(View.VISIBLE);
        layoutRegister.setVisibility(View.GONE);
        btnTabLogin.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFFE50914));
        btnTabLogin.setTextColor(0xFFFFFFFF);
        btnTabRegister.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFF141414));
        btnTabRegister.setTextColor(0xFF666666);
    }

    private void showRegisterTab() {
        layoutLogin.setVisibility(View.GONE);
        layoutRegister.setVisibility(View.VISIBLE);
        btnTabRegister.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFFE50914));
        btnTabRegister.setTextColor(0xFFFFFFFF);
        btnTabLogin.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(0xFF141414));
        btnTabLogin.setTextColor(0xFF666666);
    }

    private void checkExistingSession() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int userId = prefs.getInt(KEY_USER_ID, -1);
        String role = prefs.getString(KEY_USER_ROLE, "");
        if (userId != -1 && !role.isEmpty()) {
            navigateByRole(role);
        }
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Введите имя пользователя");
            etUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Введите пароль");
            etPassword.requestFocus();
            return;
        }

        User user = dbHelper.loginUser(username, password);

        if (user != null) {
            saveSession(user);
            Toast.makeText(this, "Добро пожаловать, " + user.getUsername() + "!", Toast.LENGTH_SHORT).show();
            navigateByRole(user.getRole());
        } else {
            Toast.makeText(this, "Неверное имя пользователя или пароль", Toast.LENGTH_LONG).show();
            etPassword.setText("");
            etPassword.requestFocus();
        }
    }

    private void performRegister() {
        String username = etRegUsername.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();
        String passwordConfirm = etRegPasswordConfirm.getText().toString().trim();

        if (username.isEmpty()) {
            etRegUsername.setError("Введите имя пользователя");
            etRegUsername.requestFocus();
            return;
        }
        if (username.length() < 3) {
            etRegUsername.setError("Минимум 3 символа");
            etRegUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etRegPassword.setError("Введите пароль");
            etRegPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etRegPassword.setError("Минимум 6 символов");
            etRegPassword.requestFocus();
            return;
        }
        if (!password.equals(passwordConfirm)) {
            etRegPasswordConfirm.setError("Пароли не совпадают");
            etRegPasswordConfirm.requestFocus();
            return;
        }

        long result = dbHelper.registerUser(username, password);

        if (result != -1) {
            Toast.makeText(this, "Аккаунт создан! Войдите в систему.", Toast.LENGTH_LONG).show();
            etRegUsername.setText("");
            etRegPassword.setText("");
            etRegPasswordConfirm.setText("");
            showLoginTab();
            etUsername.setText(username);
        } else {
            Toast.makeText(this, "Имя пользователя уже занято!", Toast.LENGTH_LONG).show();
            etRegUsername.setError("Уже занято");
            etRegUsername.requestFocus();
        }
    }

    private void openTelegram() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("tg://resolve?domain=" + TELEGRAM_USERNAME));
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://t.me/" + TELEGRAM_USERNAME));
            startActivity(intent);
        }
    }

    private void saveSession(User user) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_ROLE, user.getRole());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.apply();
    }

    private void navigateByRole(String role) {
        Intent intent;
        if ("admin".equals(role)) {
            intent = new Intent(LoginActivity.this, AdminActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}