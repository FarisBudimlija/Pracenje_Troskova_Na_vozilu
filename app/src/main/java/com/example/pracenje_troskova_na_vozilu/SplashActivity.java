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

        // pratim promjene iz viewmodela i cekam signal za prelazak
        viewModel.getNavigateToNextScreen().observe(this, shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                // idemo na login kad tajmer zavrsi
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);

                //gasenje splasha
                finish();
            }
        });

        // tajmer iz viewmodela
        viewModel.startTimer();
    }
}