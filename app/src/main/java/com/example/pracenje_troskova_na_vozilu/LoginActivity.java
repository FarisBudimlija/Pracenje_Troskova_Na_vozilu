package com.example.pracenje_troskova_na_vozilu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private EditText emailInput, passwordInput;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // povezivanje komponenti sa xml dizajnom
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);

        // povezivanje sa viewmodelom
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // pratim hocu li prebaciti korisnika na glavni ekran
        viewModel.getLoginSuccess().observe(this, success -> {
            if (success != null && success) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // pratim greske i izbacujem toast poruku
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // klik pokrece provjeru unosa u viewmodelu
        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            viewModel.loginUser(email, password);
        });
    }
}