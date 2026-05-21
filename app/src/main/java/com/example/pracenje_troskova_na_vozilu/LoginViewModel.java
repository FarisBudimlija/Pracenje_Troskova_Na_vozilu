package com.example.pracenje_troskova_na_vozilu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel extends ViewModel {

    // prijava valja?
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // instanca firebase-a za prijavu
    private final FirebaseAuth mAuth;

    public LoginViewModel() {
        // inicijalizujemo firebase kuhara za login
        mAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loginUser(String email, String password) {
        // provjera praznih polja
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Morate popuniti sva polja");
            return;
        }

        // provjera na firebase bazi da li korisnik postoji i da li je lozinka tacna
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // prijava uspjesna, pusti korisnika u aplikaciju
                        loginSuccess.setValue(true);
                    } else {
                        // greska sa firebase-a (pogresna lozinka, nepostojeci korisnik itd.)
                        if (task.getException() != null) {
                            errorMessage.setValue(task.getException().getMessage());
                        } else {
                            errorMessage.setValue("Prijava neuspjesna. Pokusajte ponovo.");
                        }
                    }
                });
    }
}