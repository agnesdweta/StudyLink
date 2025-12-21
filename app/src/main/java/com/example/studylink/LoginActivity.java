package com.example.studylink;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studylink.api.ApiService;
import com.example.studylink.api.RetrofitClient;
import com.example.studylink.db.DBHelper;
import com.example.studylink.model.LoginRequest;
import com.example.studylink.model.LoginResponse;
import com.example.studylink.util.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_pass);
        btnLogin = findViewById(R.id.btn_login);
        tvDaftar = findViewById(R.id.tv_daftar);

        btnLogin.setOnClickListener(view -> prosesLogin());

        tvDaftar.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void prosesLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan Password wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        LoginRequest request = new LoginRequest(username, password);

        api.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse res = response.body();

                    TokenManager tokenManager = new TokenManager(LoginActivity.this);
                    tokenManager.save(res.getToken(), res.getUsername());

                    DBHelper dbHelper = new DBHelper(LoginActivity.this);
                    dbHelper.insertUser(res.getUsername());

                    Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, "Username / Password salah", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Server error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
