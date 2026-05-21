package com.example.pracenje_troskova_na_vozilu;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SplashViewModel extends ViewModel {

    // objekat koji javlja aktivnosti kad da se prebaci na sledeci ekran
    private final MutableLiveData<Boolean> navigateToNextScreen = new MutableLiveData<>();

    public LiveData<Boolean> getNavigateToNextScreen() {
        return navigateToNextScreen;
    }

    public void startTimer() {
        // tajmer drzi splash 1 sekundu pa okida prelazak
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // saljem true signal da je vrijeme za promjenu ekrana
                navigateToNextScreen.setValue(true);
            }
        }, 1000);
    }
}