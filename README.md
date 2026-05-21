**Aplikacija za praćenje troškova i održavanja vozila**

  **-Korištene tehnologije i arhitektura-**
 **Razvojno okruženje:** Android Studio (Java)
 **Baza podataka na cloudu:** Firebase Firestore (trajno čuvanje korisničkih unosa)
 **Autentifikacija:** Firebase Authentication (siguran login i registracija korisnika)
 **Arhitektura:** MVVM standardi i Clean Code (izdvajanje poslovne logike)
 **Testiranje:** JUnit (Unit testovi za provjeru ispravnosti kalkulacija)

  **-Ključne Funkcionalnosti-**
1. **Splash Screen:** Početni ekran koji vizuelno najavljuje aplikaciju.
2. **Autentifikacija:** Registracija novih i prijava postojećih korisnika putem e-maila i lozinke.
3. **Praćenje finansija:** Unos cijene i litraže goriva, cijene servisa te trenutne kilometraže na satu.
4. **Pametni servisni intervali:** Automatski proračun preostalih kilometara do servisa (baza 15,000 km) sa vizuelnim upozorenjima (Zeleno/Žuto/Crveno).
5. **Korisnički Profil:** Pregled trenutno prijavljenog e-maila sa opcijama za izmjenu lozinke i odjavu sa sistema.

   **-Pokriveni Unit Testovi (JUnit)-**
Poslovna matematika aplikacije je potpuno izmještena u klasu `ServisniMetrik` i testirana kroz `ServisniMetrikTest` kako bi se garantovala tačnost proračuna:
* `testIzracunajPreostaloKM_NormalanSlucaj` - Provjera ispravne matematike preostalih kilometara.
* `testIzracunajPreostaloKM_PrekoracenjeLimita` - Provjera da li algoritam ispravno bilježi prekoračenje limita (negativna kilometraža).
* `testJeLiKilometrazaValidna_GreskaUnosa` - Validacija unosa (trenutni kilometri ne smiju biti manji od kilometara servisa).
* `testRacunajTrenutniTrosak` - Testiranje preciznosti sabiranja troškova.

   **-Pitate se kako pokrenuti?-**
1. Klonirajte repozitorij: `git clone`
2. Otvorite projekat u Android Studio okruženju.
3. Povežite projekat sa vlastitom Firebase instancom (dodavanjem `google-services.json` fajla) ili pokrenite na emulatoru.
4. Za pokretanje testova: Desni klik na folder `test` -> `Run 'ServisniMetrikTest'`.
