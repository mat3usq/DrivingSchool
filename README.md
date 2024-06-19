# Aplikacja webowa wspomagająca kurs prawa jazdy oraz szkołę jazdy

## Opis
1. Interaktywny Harmonogram Lekcji Jazdy
- Umożliwia zarówno instruktorom, jak i uczniom rezerwację i śledzenie lekcji jazdy. Zawiera kalendarz z możliwością filtrowania dostępnych terminów oraz opcję anulowania i zmiany rezerwacji.

2. Materiały Edukacyjne i Testy
- Sekcja z zasobami do nauki teorii, w tym quizami i testami. Zawiera materiały szkoleniowe, takie jak przepisy drogowe, znaki drogowe, procedury awaryjne, i inne.

3. Śledzenie Postępu Ucznia
- Funkcja, która pozwala zarówno uczniom, jak i instruktorom na śledzenie postępów w nauce. Obejmuje liczbę ukończonych godzin jazdy, oceny z testów teoretycznych i praktycznych, oraz indywidualne uwagi od instruktorów.

4. Komunikacja i Powiadomienia
- System wiadomości do komunikacji między uczniami a instruktorami oraz automatyczne przypomnienia o zbliżających się lekcjach czy testach.

5. Panel Administracyjny dla Szkoły Jazdy
- Umożliwia zarządzanie harmonogramem instruktorów, monitorowanie postępów uczniów, generowanie raportów oraz zarządzanie płatnościami.

6. Interfejs Użytkownika Przyjazny dla Użytkownika
- Aplikacja będzie intuicyjna i łatwa w obsłudze dla wszystkich osób poprzez prostotę programu.

7. Bezpieczeństwo i Prywatność Danych
- Zapewnienie wysokiego poziomu bezpieczeństwa danych osobowych, zgodnego z obowiązującymi przepisami o ochronie danych.

8. Mobilna dostępność
- Aplikacja przystosowana do wyświetlania na systemach mobilnych, co ułatwia zarządzanie szkołą jazdy oraz poprawia doświadczenie nauki dla uczniów.

9. Słowa kluczowe
- szkoła, prawo jazdy, test, egzamin, harmonogram, edukacja, testy, teoria, kursy

## Zakres
- Dogłębne zapoznanie się z tematyką prowadzenie i uczestniczenie w kursach prawa jazdy
- Analiza potrzeb i wymagań użytkowników oraz istniejących na rynku rozwiązań
- Projektowanie interfejsu użytkownika i funkcjonalności aplikacji
- Wybór technologii: Java Spring
- Implementacja aplikacji
- Testowanie i weryfikacja poprawności działania aplikacji

## TODO must have
- ~~Baza danych~~
- Dodanie strony error...
- ~~Wybór instruktora~~
- ~~Funkcja dodawania i przeglądania materiałów edukacyjnych przez studentów~~
- Rozwiązywanie testów i podział testów na kategorie
- Statystyki rozwiązanych testów
- Powiadomienia
- Komunikacja
- Zarządzanie płatnościami
- Walidacja przy rejestracji (dodanie pola confirm pwd, email)
- Walidacja przy logowaniu (email, pwd)
- Poprawka frontu + aktualnosci w dashboard
- Walidacja do dodawania wykladu/podwykladu/rozdzialu
- Przeniesienie formularzy w wykladach do sekcji nizej
- Poprawienie responsywnosci w wykladzie
- Testy: 
  - Oznaczanie pytan w testach np. gwiazdką
  - Powtarzanie pytan w testach:
    - z oznaczonymi odp, 
    - z niepoprawnymi odp, 
    - z poprawnymi odp
- Zamiana footera i ostatniej sekcji na th:fragment
- Dodanie do pytan wyjasnien bedzie lux wygladalo to wtedy 
- Dodanie zdjec do instrukcji z rozwiązywania pytan specjalistycznych oraz podstawowych

## TODO optional
- Podział na różne permisje dla kursu
- Kurs może mieć wiele kategorii
- Messenger
- Ocenianie instruktora
- Obecność na wykładach

# Harmonogram Projektu

| LP  |                                                                Zadanie                                                                 |    Okres realizacji     |
|:---:|:--------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------:|
| 1.  |                         Poszukiwanie i analiza wymagań na podstawie dostępnych na rynku rozwiązań i systemów.                          | 12.03.2024 - 19.03.2024 |
| 2.  |                                                    Stworzenie prezentacji projektu.                                                    | 19.03.2024 - 26.03.2024 |
| 3.  | Zaprojektowanie relacyjnej baza danych oraz celów wbudowania systemu. Stworzenie podstawowych pojęć związanych z projektem, diagramów. | 26.03.2024 - 16.04.2024 |
| 4.  |                         Zaimplementowanie Logowania, Rejestracji. Wdrożenie bazy danych oraz Spring Security.                          | 16.04.2024 - 23.04.2024 |
| 5.  |                                       Implementacja I etapu Funkcjonalności Wykłady, Kalendarz.                                        | 23.04.2024 - 07.05.2024 |
| 6.  |                              Implementacja II etapu Funkcjonalności (Testy, Egzaminy + poprawki I etapu).                              | 07.05.2024 - 28.05.2024 |
| 7.  | Zintegrowanie poprzednich Funkcjonalności + uzupełnienie możliwych braków. Pogrupowanie i wdrożenie przykładowej zawartości aplikacji. | 28.05.2024 - 1.07.2024  |
| 8.  |                            Implementacja III etapu Funkcjonalności (wdrożenie pozostałych funkcjonalności).                            |  1.07.2024 - 1.08.2024  |
| 9.  |                                   Poprawa widoków stron oraz Testowanie aplikacji + ewentualne poprawki.                               |  1.08.2024 - 1.10.2024  |
| 10. |                                               Wdrożenie w pełni funkcjonalnej aplikacji.                                               |  1.10.2024 - 1.11.2024  |
| 11. |                                            Napisanie i sformalizowanie pracy inżynierskiej.                                            |  1.11.2024 - 1.12.2024  |
