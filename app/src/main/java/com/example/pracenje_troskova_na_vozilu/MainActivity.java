package com.example.pracenje_troskova_na_vozilu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText inputFuelPrice, inputFuelLiters, inputCurrentKM, inputServicePrice, inputServiceKM;
    private Button btnSave, btnLogout;
    private TextView txtHistory, txtServiceStatus;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Instanca internet baze podataka
    private String userId;
    private android.widget.ImageButton btnMainProfileIcon;

    // PRIVREMENA BAZA PODATAKA (Pamti dok je aplikacija upaljena)
    private int zadnjiServisKM = 0;
    private double ukupanTrosakSveukupno = 0.0; // Ovdje sabiramo apsolutno sve troškove!

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicijalizacija Firebase-a
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Provjera da li je korisnik prijavljen i uzimanje njegovog ID-a
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        } else {
            // Ako nekim čudom nije prijavljen, vrati ga na Login
            vrateNaLogin();
            return;
        }

        // Povezivanje komponenti sa XML-om
        inputFuelPrice = findViewById(R.id.inputFuelPrice);
        inputFuelLiters = findViewById(R.id.inputFuelLiters);
        inputCurrentKM = findViewById(R.id.inputCurrentKM);
        inputServicePrice = findViewById(R.id.inputServicePrice);
        inputServiceKM = findViewById(R.id.inputServiceKM);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);
        txtHistory = findViewById(R.id.txtHistory);
        txtServiceStatus = findViewById(R.id.txtServiceStatus);

        // KORAK A: stari podaci iz baze čim se upali ovaj ekran
        ucitajPodatkeIzBaze();

        btnMainProfileIcon = findViewById(R.id.btnMainProfileIcon);

        btnMainProfileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnSave.setOnClickListener(v -> {
            double gorivoCijena = 0;
            double gorivoLitri = 0;
            double servisCijena = 0;
            int trenutniKilometri = 0;

            // 1. Pokupi podatke za gorivo i kilometre
            if (!inputFuelPrice.getText().toString().isEmpty()) {
                gorivoCijena = Double.parseDouble(inputFuelPrice.getText().toString());
            }
            if (!inputFuelLiters.getText().toString().isEmpty()) {
                gorivoLitri = Double.parseDouble(inputFuelLiters.getText().toString());
            }
            if (!inputCurrentKM.getText().toString().isEmpty()) {
                trenutniKilometri = Integer.parseInt(inputCurrentKM.getText().toString());
            }

            // 2. Pokupi podatke za servis
            if (!inputServicePrice.getText().toString().isEmpty()) {
                servisCijena = Double.parseDouble(inputServicePrice.getText().toString());
            }
            if (!inputServiceKM.getText().toString().isEmpty()) {
                zadnjiServisKM = Integer.parseInt(inputServiceKM.getText().toString());

                // Ako je tek urađen servis, trenutni kilometri su barem toliki
                if (trenutniKilometri < zadnjiServisKM) {
                    trenutniKilometri = zadnjiServisKM;
                }
            }

            // Ako korisnik nije unio ništa, prekidamo izvršavanje
            if (gorivoCijena == 0 && gorivoLitri == 0 && servisCijena == 0 && trenutniKilometri == 0) {
                Toast.makeText(MainActivity.this, "Niste unijeli nijedan podatak!", Toast.LENGTH_SHORT).show();
                return;
            }

            // KORIŠTENJE POMOĆNE KLASE: Provjera logike kilometara (Unit test uslov)
            if (!ServisniMetrik.jeLiKilometrazaValidna(trenutniKilometri, zadnjiServisKM)) {
                Toast.makeText(MainActivity.this, "Trenutni kilometri ne mogu biti manji od kilometara servisa!", Toast.LENGTH_LONG).show();
                return;
            }

            // KORIŠTENJE POMOĆNE KLASE: Računamo trenutni unos i dodajemo na SVEUKUPNI trošak
            double trenutniUnosUkupno = ServisniMetrik.racunajTrenutniTrosak(gorivoCijena, servisCijena);
            ukupanTrosakSveukupno += trenutniUnosUkupno; // Sabiramo stari iznos sa novim

            // KORAK B: Spašavanje novih ažuriranih podataka na Firebase Firestore
            sacuvajPodatkeUBazu(trenutniKilometri, gorivoCijena, gorivoLitri, servisCijena, trenutniUnosUkupno);
        });

        // Odjava
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            vrateNaLogin();
        });
    }

    private void sacuvajPodatkeUBazu(int trenutniKM, double gorivoC, double gorivoL, double servisC, double trenutniUkupno) {
        // Kreiramo mapu podataka koju šaljemo u Firestore pod ID-em korisnika
        Map<String, Object> autoPodaci = new HashMap<>();
        autoPodaci.put("zadnjiServisKM", zadnjiServisKM);
        autoPodaci.put("ukupanTrosakSveukupno", ukupanTrosakSveukupno);
        autoPodaci.put("trenutnaKilometraza", trenutniKM);

        db.collection("korisnici").document(userId)
                .set(autoPodaci)
                .addOnSuccessListener(aVoid -> {
                    // Ako je uspješno sačuvano na internetu, osvježi prikaz na ekranu
                    osveziEkran(trenutniKM, gorivoC, gorivoL, servisC, trenutniUkupno);

                    // 6. RESETUJ POLJA ZA UNOS
                    inputFuelPrice.setText("");
                    inputFuelLiters.setText("");
                    inputCurrentKM.setText("");
                    inputServicePrice.setText("");
                    inputServiceKM.setText("");

                    Toast.makeText(MainActivity.this, "Podaci trajno sačuvani na cloudu!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Greška pri spašavanju: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void ucitajPodatkeIzBaze() {
        db.collection("korisnici").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Ako korisnik već ima podatke u bazi, povuci ih u aplikaciju
                        if (documentSnapshot.contains("zadnjiServisKM")) {
                            zadnjiServisKM = documentSnapshot.getLong("zadnjiServisKM").intValue();
                        }
                        if (documentSnapshot.contains("ukupanTrosakSveukupno")) {
                            ukupanTrosakSveukupno = documentSnapshot.getDouble("ukupanTrosakSveukupno");
                        }

                        int trenutniKM = 0;
                        if (documentSnapshot.contains("trenutnaKilometraza")) {
                            trenutniKM = documentSnapshot.getLong("trenutnaKilometraza").intValue();
                        }

                        // Prikaži učitane podatke na ekranu čim se aplikacija upali
                        osveziEkran(trenutniKM, 0, 0, 0, 0);
                    }
                });
    }

    private void osveziEkran(int trenutniKilometri, double gorivoC, double gorivoL, double servisC, double trenutniUkupno) {
        // 4. ISPIS FINANSIJA NA EKRAN
        String ispisFinansija = "Zadnji unos:\n" +
                "⛽ Gorivo: " + gorivoC + " KM (" + gorivoL + " L)\n" +
                "🔧 Servis: " + servisC + " KM\n" +
                "📍 Kilometraža na satu: " + trenutniKilometri + " km\n" +
                "-------------------------\n" +
                "💰 Potrošeno u ovom unosu: " + trenutniUkupno + " KM\n\n" +
                "📈 SVEUKUPAN TROŠAK VOZILA:\n" +
                "💵 " + ukupanTrosakSveukupno + " KM";

        txtHistory.setText(ispisFinansija);

        // 5. LOGIKA ZA SERVIS (15.000 km ili 365 dana)
        if (zadnjiServisKM > 0 && trenutniKilometri >= zadnjiServisKM) {

            // KORIŠTENJE POMOĆNE KLASE: Računanje preostalih kilometara kroz izdvojenu logiku
            int preostaloKM = ServisniMetrik.izracunajPreostaloKM(trenutniKilometri, zadnjiServisKM);
            int preostaloDana = 365;

            // Slučaj A: Korisnik je već prešao 15.000 km
            if (preostaloKM <= 0) {
                txtServiceStatus.setText("⚠️ HITNO NA SERVIS!\nPrešli ste limit za " + Math.abs(preostaloKM) + " km!");
                txtServiceStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
            // Slučaj B: Ostalo je manje od 1000 km do servisa
            else if (preostaloKM <= 1000) {
                txtServiceStatus.setText("⚠️ PAŽNJA!\nOstalo vam je još samo " + preostaloKM + " km do servisa!");
                txtServiceStatus.setTextColor(Color.parseColor("#FFD700"));
            }
            // Slučaj C: Sve je u redu, ima još dosta do servisa
            else {
                String statusPoruka = "🚗 Do servisa preostalo još:\n" +
                        "📍 " + preostaloKM + " km\n" +
                        "📅 " + preostaloDana + " dana";
                txtServiceStatus.setText(statusPoruka);
                txtServiceStatus.setTextColor(getResources().getColor(android.R.color.black));
            }
        } else if (zadnjiServisKM == 0) {
            txtServiceStatus.setText("Molimo unesite kilometražu zadnjeg servisa kako bi aplikacija izračunala rok.");
        }
    }

    //vracanje na login ide preko splasha
    private void vrateNaLogin() {
        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        intent.putExtra("SLEDECI_EKRAN", "LOGIN"); // saljemo signal za login
        startActivity(intent);
        finish();
    }
}