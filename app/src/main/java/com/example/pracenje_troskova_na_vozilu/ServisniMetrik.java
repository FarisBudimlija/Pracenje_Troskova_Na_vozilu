package com.example.pracenje_troskova_na_vozilu;

public class ServisniMetrik {

    // Računa preostale kilometre do servisa (Limit je 15.000 km)
    public static int izracunajPreostaloKM(int trenutniKM, int zadnjiServisKM) {
        if (zadnjiServisKM <= 0) return 15000;
        int predjenoOdServisa = trenutniKM - zadnjiServisKM;
        return 15000 - predjenoOdServisa;
    }

    // Provjerava da li je kilometraža ispravna (trenutna ne smije biti manja od zadnjeg servisa)
    public static boolean jeLiKilometrazaValidna(int trenutniKM, int zadnjiServisKM) {
        if (zadnjiServisKM > 0 && trenutniKM < zadnjiServisKM) {
            return false;
        }
        return true;
    }

    // Sabira trenutni unos goriva i servisa u ukupan trenutni trošak
    public static double racunajTrenutniTrosak(double gorivoCijena, double servisCijena) {
        return gorivoCijena + servisCijena;
    }
}