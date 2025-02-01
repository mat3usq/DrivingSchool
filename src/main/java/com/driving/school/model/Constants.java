package com.driving.school.model;

import java.util.Arrays;
import java.util.List;

public interface Constants {
    // Role
    String STUDENT_ROLE = "STUDENT";
    String INSTRUCTOR_ROLE = "INSTRUCTOR";
    String ADMIN_ROLE = "ADMIN";

    // Instruction meetings
    String INTRODUCTORY_MEETING = "Spotkanie Wprowadzające";
    String ORGANIZATIONAL_SESSION = "Sesja Jazdy";
    String BASIC_OVERVIEW = "Podstawowy Przegląd";
    String THEORY_PRESENTATION = "Prezentacja Teoretyczna";
    String PRACTICAL_INTRODUCTION = "Wprowadzenie Praktyczne";
    String SAFETY_WORKSHOP = "Warsztat Bezpieczeństwa";
    String ROAD_RULES_SEMINAR = "Seminarium Przepisów Drogowych";
    String FIRST_AID_COURSE = "Kurs Pierwszej Pomocy";
    String DRIVER_PREPARATION = "Przygotowanie Kierowcy";
    String CITY_DRIVING_TALK = "Prelekcja o Jeździe Miejskiej";
    String HIGHWAY_DRIVING_TRAINING = "Szkolenie z Jazdy Autostradowej";
    String NIGHT_DRIVING_OVERVIEW = "Przegląd Jazdy Nocnej";
    String ECO_DRIVING_DISCUSSION = "Dyskusja o Jeździe Ekonomicznej";
    String EMERGENCY_BRAKING_DRILL = "Ćwiczenia Gwałtownego Hamowania";
    String PRACTICAL_EXAM_TIPS = "Wskazówki do Egzaminu Praktycznego";
    String THEORETICAL_EXAM_GUIDE = "Poradnik Egzaminu Teoretycznego";
    String GROUP_DISCUSSION = "Dyskusja Grupowa";
    String TRAFFIC_SIGNS_REVIEW = "Przegląd Znaków Drogowych";
    String BASIC_MANEUVERS_PRACTICE = "Ćwiczenie Podstawowych Manewrów";
    String PASSENGER_SAFETY_OVERVIEW = "Przegląd Bezpieczeństwa Pasażerów";
    String FINAL_REVISION = "Ostateczna Powtórka";
    String EMERGENCY_PROCEDURES_SEMINAR = "Seminarium Procedur Awaryjnych";
    String PRACTICAL_QA_SESSION = "Sesja Pytań i Odpowiedzi Praktycznych";
    String INTRO_TO_MANUAL_TRANSMISSION = "Wprowadzenie do Skrzyni Manualnej";
    String INTRO_TO_AUTOMATIC_TRANSMISSION = "Wprowadzenie do Skrzyni Automatycznej";
    String RURAL_DRIVING_GUIDE = "Poradnik Jazdy Poza Miastem";
    String SUBURBAN_TEST_DRIVE = "Testowe Jazdy Podmiejskie";
    String REFRESHER_LESSONS = "Lekcje Przypominające";
    String PRACTICAL_MANEUVERING_SESSION = "Sesja Manewrowania w Praktyce";
    String STRATEGIC_DRIVING_TACTICS = "Taktyki Strategicznej Jazdy";
    String CRITICAL_DECISION_TRAINING = "Szkolenie z Krytycznych Decyzji";
    String DEFENSIVE_DRIVING_SEMINAR = "Seminarium z Jazdy Defensywnej";
    String EMERGENCY_STOP_TRAINING = "Szkolenie z Hamowania Awaryjnego";
    String PRACTICAL_EXAM_PRACTICE = "Praktyka przed Egzaminem";
    String HAZARD_PERCEPTION_TRAINING = "Szkolenie z Rozpoznawania Zagrożeń";
    String EXAM_SIMULATION = "Symulacja Egzaminu Końcowego";
    String THEORY_PRACTICE_CONNECT = "Połączenie Teorii z Praktyką";
    String ACCIDENT_PREVENTION_TALK = "Prelekcja o Zapobieganiu Wypadkom";
    String SPEED_MANAGEMENT_WORKSHOP = "Warsztat Zarządzania Prędkością";
    String ADAPTIVE_DRIVING_SEMINAR = "Seminarium z Jazdy Adaptacyjnej";
    String LANE_CHANGING_EXERCISES = "Ćwiczenia Zmiany Pasów Ruchu";
    String HEADLIGHT_USAGE_WORKSHOP = "Warsztat Użycia Świateł";
    String MOUNTAIN_ROAD_GUIDANCE = "Instruktaż Jazdy Górskiej";
    String WINTER_DRIVING_PREPARATION = "Przygotowanie do Jazdy Zimowej";
    String VEHICLE_MAINTENANCE_TALK = "Prelekcja o Utrzymaniu Pojazdu";
    String DOCUMENTATION_WORKSHOP = "Warsztat Dokumentacyjny";
    String ROAD_SAFETY_DISCUSSION = "Dyskusja o Bezpieczeństwie na Drodze";
    String ROAD_ETIQUETTE_DISCUSSION = "Dyskusja o Kulturze na Drodze";
    String GROUP_REFLECTION_MEETING = "Spotkanie Refleksyjne w Grupie";
    String SUMMARY_MEETING = "Spotkanie Podsumowujące";
    String CLOSING_CEREMONY = "Ceremonia Zakończenia";
    String OTHER = "Inne";

    static List<String> getAllEventTypes() {
        return Arrays.asList(INTRODUCTORY_MEETING, ORGANIZATIONAL_SESSION, BASIC_OVERVIEW, THEORY_PRESENTATION, PRACTICAL_INTRODUCTION, SAFETY_WORKSHOP, ROAD_RULES_SEMINAR, FIRST_AID_COURSE, DRIVER_PREPARATION, CITY_DRIVING_TALK, HIGHWAY_DRIVING_TRAINING, NIGHT_DRIVING_OVERVIEW, ECO_DRIVING_DISCUSSION, EMERGENCY_BRAKING_DRILL, PRACTICAL_EXAM_TIPS, THEORETICAL_EXAM_GUIDE, GROUP_DISCUSSION, TRAFFIC_SIGNS_REVIEW, BASIC_MANEUVERS_PRACTICE, PASSENGER_SAFETY_OVERVIEW, FINAL_REVISION, EMERGENCY_PROCEDURES_SEMINAR, PRACTICAL_QA_SESSION, INTRO_TO_MANUAL_TRANSMISSION, INTRO_TO_AUTOMATIC_TRANSMISSION, RURAL_DRIVING_GUIDE, SUBURBAN_TEST_DRIVE, REFRESHER_LESSONS, PRACTICAL_MANEUVERING_SESSION, STRATEGIC_DRIVING_TACTICS, CRITICAL_DECISION_TRAINING, DEFENSIVE_DRIVING_SEMINAR, EMERGENCY_STOP_TRAINING, PRACTICAL_EXAM_PRACTICE, HAZARD_PERCEPTION_TRAINING, EXAM_SIMULATION, THEORY_PRACTICE_CONNECT, ACCIDENT_PREVENTION_TALK, SPEED_MANAGEMENT_WORKSHOP, ADAPTIVE_DRIVING_SEMINAR, LANE_CHANGING_EXERCISES, HEADLIGHT_USAGE_WORKSHOP, MOUNTAIN_ROAD_GUIDANCE, WINTER_DRIVING_PREPARATION, VEHICLE_MAINTENANCE_TALK, DOCUMENTATION_WORKSHOP, ROAD_SAFETY_DISCUSSION, ROAD_ETIQUETTE_DISCUSSION, GROUP_REFLECTION_MEETING, SUMMARY_MEETING, CLOSING_CEREMONY, OTHER);
    }

    // MentorShip Student - Instructor
    String ACTIVE = "W Trakcie";
    String COMPLETED = "Zakonczony";
    String PENDING = "Oczekiwanie";
    String CANCELLED = "Anulowany";
    String SUSPENDED = "Zawieszony";
    String TERMINATED = "Zakonczony Przed";
    String EXPIRED = "Wygasly";


    // Mail Status
    String MAIL_UNREAD = "UNREAD";
    String MAIL_READ = "READ";
    String MAIL_REPLY = "REPLY";
    String MAIL_TRASHED = "TRASHED";
    String MAIL_DELETED = "DELETED";

    // Course Status
    String COURSE_PASSED = "Pozytywny";
    String COURSE_FAILED = "Negatywny";
    String COURSE_NOTSPECIFIED = "Brak";

    // Course TEST types
    String COURSE_TEST_THEORETICAL = "Test Teoretyczny";
    String COURSE_TEST_PRACTICAL = "Test Praktyczny";
    String COURSE_TEST_GENERAL = "Test Ogólny";

    // Notification Status
    String NOTIFICATION_NOT_SEEN = "NOT_SEEN";
    String NOTIFICATION_SEEN = "SEEN";
}
