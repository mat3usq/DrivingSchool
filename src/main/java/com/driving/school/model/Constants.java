package com.driving.school.model;

import java.util.Arrays;
import java.util.List;

public interface Constants {
    // Role
    String STUDENT_ROLE = "STUDENT";
    String INSTRUCTOR_ROLE = "INSTRUCTOR";
    String ADMIN_ROLE = "ADMIN";

    // Spotkania
    String THEORETICAL_CLASSES = "Teoretyczne Zajęcia";
    String LECTURES = "Wykłady";
    String FIRST_AID_CLASSES = "Zajęcia z Pierwszej Pomocy";
    String THEORY_OF_DRIVING_CLASSES = "Zajęcia z Teorii Jazdy";
    String PRACTICAL_CLASSES_ON_MANEUVERING_GROUND = "Praktyczne Zajęcia na Placu Manewrowym";
    String BASIC_MANEUVERS = "Podstawowe Manewry";
    String PRACTICAL_DRIVING_ON_ROADS = "Praktyczne Jazdy po Drogach";
    String CITY_DRIVING = "Jazdy Miejskie";
    String OUT_OF_TOWN_DRIVING = "Jazdy Poza Miastem";
    String NIGHT_DRIVING = "Jazdy Nocne";
    String SPECIALIZED_CLASSES = "Specjalistyczne Zajęcia";
    String ADDITIONAL_DRIVING_LESSONS = "Jazdy Doszkalające";
    String EMERGENCY_TECHNIQUES_TRAINING = "Szkolenia z Technik Awaryjnych";
    String ECO_DRIVING_CLASSES = "Jazdy Ekonomiczne";
    String CAR_MAINTENANCE_TRAINING = "Szkolenia z Obsługi Samochodu";
    String INFORMATION_AND_ORGANIZATIONAL_MEETINGS = "Spotkania Informacyjne i Organizacyjne";
    String INFORMATION_SESSIONS = "Sesje Informacyjne";
    String MEETINGS_WITH_EXAMINERS = "Spotkania z Egzaminatorami";
    String OTHER = "Inne";

    static List<String> getAllEventTypes() {
        return Arrays.asList(
                THEORETICAL_CLASSES,
                LECTURES,
                FIRST_AID_CLASSES,
                THEORY_OF_DRIVING_CLASSES,
                PRACTICAL_CLASSES_ON_MANEUVERING_GROUND,
                BASIC_MANEUVERS,
                PRACTICAL_DRIVING_ON_ROADS,
                CITY_DRIVING,
                OUT_OF_TOWN_DRIVING,
                NIGHT_DRIVING,
                SPECIALIZED_CLASSES,
                ADDITIONAL_DRIVING_LESSONS,
                EMERGENCY_TECHNIQUES_TRAINING,
                ECO_DRIVING_CLASSES,
                CAR_MAINTENANCE_TRAINING,
                INFORMATION_AND_ORGANIZATIONAL_MEETINGS,
                INFORMATION_SESSIONS,
                MEETINGS_WITH_EXAMINERS,
                OTHER
        );
    }

    // Relacja Student - Instruktor
    String ACTIVE = "W Trakcie";
    String COMPLETED = "Zakonczony";
    String PENDING = "Oczekiwanie";
    String CANCELLED = "Anulowany";
    String SUSPENDED = "Zawieszony";
    String TERMINATED = "Zakonczony Przed";
    String EXPIRED = "Wygasly";
}
