package com.example.pracenje_troskova_na_vozilu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private EditText editEmail, editOldPassword, editNewPassword;
    private Button btnSave;
    private FirebaseAuth mAuth;
    private FirebaseUser korisnik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button btnBack = findViewById(R.id.btnBackToMain);
        btnBack.setOnClickListener(v -> {
            finish(); // Zatvara ProfileActivity i vraća na MainActivity
        });

        mAuth = FirebaseAuth.getInstance();
        korisnik = mAuth.getCurrentUser();

        editEmail = findViewById(R.id.editProfileEmail);
        editOldPassword = findViewById(R.id.editProfileOldPassword);
        editNewPassword = findViewById(R.id.editProfileNewPassword);
        btnSave = findViewById(R.id.btnProfileSave);

        // Prikaži trenutni email prijavljenog korisnika
        if (korisnik != null) {
            editEmail.setText(korisnik.getEmail());
        }

        btnSave.setOnClickListener(v -> {
            String noviEmail = editEmail.getText().toString().trim();
            String staraSifra = editOldPassword.getText().toString().trim();
            String novaSifra = editNewPassword.getText().toString().trim();

            if (staraSifra.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Morate unijeti staru šifru radi potvrde identiteta!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (korisnik != null) {
                // Provjera stare šifre (Reautentifikacija na Firebase serveru)
                AuthCredential credential = EmailAuthProvider.getCredential(korisnik.getEmail(), staraSifra);

                korisnik.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        // 1. Ako se email promijenio, ažuriraj ga
                        if (!noviEmail.equals(korisnik.getEmail())) {
                            korisnik.updateEmail(noviEmail).addOnCompleteListener(emailTask -> {
                                if (emailTask.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Email uspješno izmijenjen!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Greška pri promjeni emaila!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        // 2. Ako je unesena nova šifra, ažuriraj je
                        if (!novaSifra.isEmpty()) {
                            if (novaSifra.length() < 6) {
                                Toast.makeText(ProfileActivity.this, "Nova šifra mora imati barem 6 znakova!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            korisnik.updatePassword(novaSifra).addOnCompleteListener(passTask -> {
                                if (passTask.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Šifra uspješno izmijenjena!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        Toast.makeText(ProfileActivity.this, "Profil uspješno ažuriran!", Toast.LENGTH_SHORT).show();
                        finish(); // Zatvara profil i vraća te na MainActivity

                    } else {
                        Toast.makeText(ProfileActivity.this, "Pogrešna stara šifra!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}