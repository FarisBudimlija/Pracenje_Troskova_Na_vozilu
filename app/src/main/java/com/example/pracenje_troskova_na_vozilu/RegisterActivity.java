package com.example.pracenje_troskova_na_vozilu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel viewModel;
    private EditText emailRegInput, passwordRegInput, confirmPasswordInput;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // povezivanje komponenti sa xml-om
        emailRegInput = findViewById(R.id.emailRegInput);
        passwordRegInput = findViewById(R.id.passwordRegInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerBtn = findViewById(R.id.registerBtn);

        // inicijalizacija viewmodela
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // registracija valja? ako valja, vodi na login ekran
        viewModel.getRegisterSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(RegisterActivity.this, "Registracija uspjesna!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // greska sa unosa? izbaci je u toast poruci
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // klik na dugme kupi podatke sa ekrana i salje u viewmodel na provjeru
        registerBtn.setOnClickListener(v -> {
            String email = emailRegInput.getText().toString().trim();
            String password = passwordRegInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            viewModel.registerUser(email, password, confirmPassword);
        });
    }
}