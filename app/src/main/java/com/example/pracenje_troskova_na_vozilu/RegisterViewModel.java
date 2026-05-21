package com.example.pracenje_troskova_na_vozilu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {

    // registracija valja?
    private final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void registerUser(String email, String password, String confirmPassword) {
        // provjera praznih polja
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorMessage.setValue("Morate popuniti sva polja");
            return;
        }

        // provjera lozinke
        if (!password.equals(confirmPassword)) {
            errorMessage.setValue("Lozinke se ne podudaraju");
            return;
        }

        // ovdje ide firebase registracija kasnije
        // za sada simuliramo uspjeh radi testiranja navigacije
        registerSuccess.setValue(true);
    }
}