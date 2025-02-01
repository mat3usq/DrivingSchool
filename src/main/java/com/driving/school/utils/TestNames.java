package com.driving.school.utils;

import lombok.Getter;

import java.util.ArrayList;

public class TestNames {
    @Getter
    private static final ArrayList<String> names = new ArrayList<>();
    @Getter
    private static final ArrayList<String> specialTestNames = new ArrayList<>();

    static {
        names.add("Znaki Ostrzegawcze");
        names.add("Znaki nakazu i zakazu");
        names.add("Znaki informacyjne, kierunku");
        names.add("Znaki poziome");
        names.add("Sygnalizacja świetlna");
        names.add("Pierwszeństwo przejazdu");
        names.add("Zatrzymanie, postój, włączenie się do ruchu");
        names.add("Zmiana kierunku jazdy, przejazdy kolejowe");
        names.add("Wyprzedzanie, omijanie, wymijanie cofanie");
        names.add("Szczególna ostrożność");
        names.add("Warunki drogowe");
        names.add("Prędkość, hamowanie, odstępy");
        names.add("Technika jazdy");
        names.add("Obowiązki kierującego, dokumenty");
        names.add("Awaria pojazdu, pierwsza pomoc");
        names.add("Inne");
        specialTestNames.add("Znaki Ostrzegawcze");
        specialTestNames.add("Znaki nakazu i zakazu");
        specialTestNames.add("Znaki informacyjne, kierunku");
        specialTestNames.add("Znaki poziome");
        specialTestNames.add("Sygnalizacja świetlna");
        specialTestNames.add("Pierwszeństwo przejazdu");
        specialTestNames.add("Zatrzymanie, postój, włączenie się do ruchu");
        specialTestNames.add("Zmiana kierunku jazdy, przejazdy kolejowe");
        specialTestNames.add("Wyprzedzanie, omijanie, wymijanie cofanie");
        specialTestNames.add("Szczególna ostrożność");
        specialTestNames.add("Warunki drogowe");
        specialTestNames.add("Prędkość, hamowanie, odstępy");
        specialTestNames.add("Technika jazdy");
        specialTestNames.add("Obowiązki kierującego, dokumenty");
        specialTestNames.add("Awaria pojazdu, pierwsza pomoc");
        specialTestNames.add("Inne");
    }
}
