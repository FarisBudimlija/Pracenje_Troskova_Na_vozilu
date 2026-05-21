package com.example.pracenje_troskova_na_vozilu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterViewModel extends ViewModel {

    // registracija valja?
    private final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // instanca firebase-a za registraciju
    private final FirebaseAuth mAuth;

    public RegisterViewModel() {
        // inicijalizujemo firebase
        mAuth = FirebaseAuth.getInstance();
    }

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

        // Firebase zahtijeva da lozinka ima minimalno 6 znakova
        if (password.length() < 6) {
            errorMessage.setValue("Lozinka must imati najmanje 6 znakova");
            return;
        }

        // ŠALJEMO NA INTERNET:
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // registracija uspjela na Firebase-u!
                        registerSuccess.setValue(true);
                    } else {
                        // Ako Firebase vrati grešku (npr. email već postoji ili nema interneta)
                        if (task.getException() != null) {
                            errorMessage.setValue("Firebase greška: " + task.getException().getMessage());
                        } else {
                            errorMessage.setValue("Registracija neuspjesna. Pokusajte ponovo.");
                        }
                    }
                });
    }
}