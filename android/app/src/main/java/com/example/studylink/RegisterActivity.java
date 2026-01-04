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
import com.example.studylink.model.RegisterRequest;
import com.example.studylink.model.RegisterResponse;
import com.example.studylink.util.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPass;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Hubungkan XML
        etUsername     = findViewById(R.id.et_reg_username);
        etPassword     = findViewById(R.id.et_reg_password);
        etConfirmPass  = findViewById(R.id.et_reg_confirm_pass);
        btnRegister    = findViewById(R.id.btn_register);
        tvLogin        = findViewById(R.id.tv_login);

        btnRegister.setOnClickListener(v -> prosesRegister());
        tvLogin.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }

    private void prosesRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm  = etConfirmPass.getText().toString().trim();

        // Validasi input
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Password dan konfirmasi tidak sama", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getService();
        RegisterRequest request = new RegisterRequest(username, password);

        api.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse res = response.body();
                    TokenManager tokenManager = new TokenManager(RegisterActivity.this);
                    tokenManager.save(res.getToken(), res.getUsername(), res.getUserId());

                    Toast.makeText(RegisterActivity.this, "Register berhasil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Register gagal (username sudah dipakai)",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                        "Server error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
