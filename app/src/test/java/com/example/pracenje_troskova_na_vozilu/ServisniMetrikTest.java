package com.example.pracenje_troskova_na_vozilu;

import org.junit.Test;
import static org.junit.Assert.*;

public class ServisniMetrikTest {

    @Test
    public void testIzracunajPreostaloKM_NormalanSlucaj() {
        // Ako je zadnji servis bio na 50.000 km, a trenutno je 55.000 km, pređeno je 5.000 km.
        // Od limitiranih 15.000 km, trebalo bi ostati još tačno 10.000 km do servisa.
        int preostalo = ServisniMetrik.izracunajPreostaloKM(55000, 50000);
        assertEquals(10000, preostalo);
    }

    @Test
    public void testIzracunajPreostaloKM_PrekoracenjeLimita() {
        // Ako je trenutno 66.000 km, a servis rađen na 50.000 km, prešli smo 16.000 km od servisa.
        // Rezultat treba biti negativan (-1000 km) što znači da kasnimo na servis.
        int preostalo = ServisniMetrik.izracunajPreostaloKM(66000, 50000);
        assertEquals(-1000, preostalo);
    }

    @Test
    public void testJeLiKilometrazaValidna_GreskaUnosa() {
        // Trenutni kilometri ne mogu biti manji od kilometara zadnjeg servisa (npr. 45000 < 50000)
        // Funkcija mora prepoznati grešku i vratiti 'false'
        boolean rezultat = ServisniMetrik.jeLiKilometrazaValidna(45000, 50000);
        assertFalse(rezultat);
    }

    @Test
    public void testRacunajTrenutniTrosak() {
        // Provjera da li naša klasa ispravno sabira troškove goriva i servisa
        double ukupanTrosak = ServisniMetrik.racunajTrenutniTrosak(70.50, 120.00);
        assertEquals(190.50, ukupanTrosak, 0.001);
    }
}