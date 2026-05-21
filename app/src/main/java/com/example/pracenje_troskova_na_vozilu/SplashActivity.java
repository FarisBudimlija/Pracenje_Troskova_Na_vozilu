package com.example.pracenje_troskova_na_vozilu;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class SplashActivity extends AppCompatActivity {

    private SplashViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // povezujem viewmodel sa aktivnosti
        viewModel = new ViewModelProvider(this).get(SplashViewModel.class);

        // uzimam informaciju na koji ekran treba da idemo
        String sledeciEkran = getIntent().getStringExtra("SLEDECI_EKRAN");
        if (sledeciEkran == null) {
            sledeciEkran = "LOGIN"; // ako nema nista ide na login
        }

        final String konacnoOdrediste = sledeciEkran;

        // pratim promjene iz viewmodela i cekam signal za prelazak
        viewModel.getNavigateToNextScreen().observe(this, shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                Intent intent;

                // skretnica zavisno od toga sta je proslijedjeno
                switch (konacnoOdrediste) {
                    case "MAIN":
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                        break;
                    case "PROFILE":
                        intent = new Intent(SplashActivity.this, ProfileActivity.class);
                        break;
                    case "REGISTER":
                        intent = new Intent(SplashActivity.this, RegisterActivity.class);
                        break;
                    case "LOGIN":
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                        break;
                    default:
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                        break;
                }

                startActivity(intent);

                // gasenje splasha
                finish();
            }
        });

        // tajmer iz viewmodela
        viewModel.startTimer();
    }
}