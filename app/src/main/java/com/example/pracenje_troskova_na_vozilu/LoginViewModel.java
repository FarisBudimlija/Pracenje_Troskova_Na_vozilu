package com.example.pracenje_troskova_na_vozilu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    // pratim da li je prijava na aplikaciju uspjesna
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    // pratm tekst greke ako unosi nisu dobri
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Popunite sva polja");
            return;
        }


        loginSuccess.setValue(true);
    }
}