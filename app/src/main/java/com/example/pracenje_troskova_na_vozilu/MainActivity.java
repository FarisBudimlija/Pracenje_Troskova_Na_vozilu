package com.example.pracenje_troskova_na_vozilu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText inputFuelPrice, inputFuelLiters, inputCurrentKM, inputServicePrice, inputServiceKM;
    private Button btnSave, btnLogout;
    private TextView txtHistory, txtServiceStatus;
    private FirebaseAuth mAuth;

    // PRIVREMENA BAZA PODATAKA (Pamti dok je aplikacija upaljena)
    private int zadnjiServisKM = 0;
    private double ukupanTrosakSveukupno = 0.0; // Ovdje sabiramo apsolutno sve troškove!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

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

            // Provjera logike kilometara prije nego što išta saberemo
            if (zadnjiServisKM > 0 && trenutniKilometri < zadnjiServisKM) {
                Toast.makeText(MainActivity.this, "Trenutni kilometri ne mogu biti manji od kilometara servisa!", Toast.LENGTH_LONG).show();
                return;
            }

            // 3. MATEMATIKA: Računamo trenutni unos i dodajemo na SVEUKUPNI trošak
            double trenutniUnosUkupno = gorivoCijena + servisCijena;
            ukupanTrosakSveukupno += trenutniUnosUkupno; // Sabiramo stari iznos sa novim

            // 4. ISPIS FINANSIJA NA EKRAN
            String ispisFinansija = "Zadnji unos:\n" +
                    "⛽ Gorivo: " + gorivoCijena + " KM (" + gorivoLitri + " L)\n" +
                    "🔧 Servis: " + servisCijena + " KM\n" +
                    "📍 Kilometraža na satu: " + trenutniKilometri + " km\n" +
                    "-------------------------\n" +
                    "💰 Potrošeno u ovom unosu: " + trenutniUnosUkupno + " KM\n\n" +
                    "📈 SVEUKUPAN TROŠAK VOZILA:\n" +
                    "💵 " + ukupanTrosakSveukupno + " KM";

            txtHistory.setText(ispisFinansija);

            // 5. LOGIKA ZA SERVIS (15.000 km ili 365 dana)
            if (zadnjiServisKM > 0 && trenutniKilometri >= zadnjiServisKM) {
                int predjenoOdServisa = trenutniKilometri - zadnjiServisKM;
                int preostaloKM = 15000 - predjenoOdServisa;
                int preostaloDana = 365;

                // Slučaj A: Korisnik je već prešao 15.000 km -> CRVENO UPOZORENJE
                if (preostaloKM <= 0) {
                    txtServiceStatus.setText("⚠️ HITNO NA SERVIS!\nPrešli ste limit za " + Math.abs(preostaloKM) + " km!");
                    txtServiceStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
                // Slučaj B: Ostalo je manje od 1000 km do servisa -> ŽUTO/NARANDŽASTO UPOZORENJE
                else if (preostaloKM <= 1000) {
                    txtServiceStatus.setText("⚠️ PAŽNJA!\nOstalo vam je još samo " + preostaloKM + " km do servisa!");
                    txtServiceStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                    // Napomena: Ako baš želiš drečavu "neon" žutu, umjesto holo_orange_dark stavi: Color.parseColor("#FFD700")
                }
                // Slučaj C: Sve je u redu, ima još dosta do servisa -> CRNA/ZELENA BOJA
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

            // 6. RESETUJ POLJA ZA UNOS
            inputFuelPrice.setText("");
            inputFuelLiters.setText("");
            inputCurrentKM.setText("");
            inputServicePrice.setText("");
            inputServiceKM.setText("");

            Toast.makeText(MainActivity.this, "Podaci uspješno ažurirani!", Toast.LENGTH_SHORT).show();
        });

        // Odjava
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}