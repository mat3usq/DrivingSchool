package com.driving.school.utils;

import java.util.ArrayList;

public class TestNames {
    private static final ArrayList<String> names = new ArrayList<>();
    private static final ArrayList<String> specialtestnames = new ArrayList<>();

    static {
        names.add("Znaki Ostrzegawcze");
        names.add("Znaki nakazu i zakazu");
        names.add("Znaki informacyjne, kierunku");
        names.add("Znaki poziome");
        names.add("Sygnalizacja świetlna");
        names.add("Pierwszeństwo przejazdu");
        names.add("Zatrzymanie, postój, włączenie się do ruchu");
        names.add("Zmiana kieruynku jazdy, przejazdy kolejowe");
        names.add("Wyprzedzanie, omijanie, wymijanie cofanie");
        names.add("Szczególna ostrożnosć");
        names.add("Warunki drogowe");
        names.add("Technika jazdy");
        names.add("Obowiązki kierującego,dokumenty");
        names.add("Awaria pojazdu, pierwsza pomoc");
        names.add("Inne");
        specialtestnames.add("Warunki drogowe");
        specialtestnames.add("Prędkość hamowania");
        specialtestnames.add("Technika jazdy");
        specialtestnames.add("Obowiązki kierującego, dokumenty");
        specialtestnames.add("Awaria pojazdu, pierwsza pmoc");
        specialtestnames.add("Inne");
    }

    public static ArrayList<String> getNames() {
        return names;
    }

    public static ArrayList<String> getSpecialtestnames() {
        return specialtestnames;
    }
}