package com.driving.school.utils;

import com.driving.school.model.*;
import com.driving.school.repository.*;
import com.driving.school.service.QuestionService;
import com.driving.school.service.TestService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final QuestionService questionService;
    private final SchoolUserRepository schoolUserRepository;
    private final LectureRepository lectureRepository;
    private final SublectureRepository sublectureRepository;
    private final SubjectRepository subjectRepository;
    private final TestService testService;
    private final InstructionEventRepository eventRepository;
    private final StudentInstructorRepository studentInstructorRepository;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    @Autowired
    public DatabaseSeeder(QuestionService questionService, SchoolUserRepository
            schoolUserRepository, LectureRepository lectureRepository, SublectureRepository sublectureRepository,
                          SubjectRepository subjectRepository, TestService testService, InstructionEventRepository eventRepository, StudentInstructorRepository studentInstructorRepository) {
        this.questionService = questionService;
        this.schoolUserRepository = schoolUserRepository;
        this.lectureRepository = lectureRepository;
        this.sublectureRepository = sublectureRepository;
        this.subjectRepository = subjectRepository;
        this.testService = testService;
        this.eventRepository = eventRepository;
        this.studentInstructorRepository = studentInstructorRepository;
    }

    @Override
    public void run(String... args) {
        createUsers();
        createLectures();
        addTeststoDb();
        mapQuestionsToDb();
        createEvents();
    }

    private void createUsers() {
        // haslo: admin
        SchoolUser admin = new SchoolUser("admin", "admin", "$100801$cfJJlxSl83FjJ2mh+6yUcdxxksVm3XlOzhBr4gLHFEOhdeWmaf2H6Lki/fe99YUMduDoX/LGHUcodWe9SkhVnw==$Q1yyulzoYXseBJ/OmM/xgcYD9fFPaw2bRzBHRW7RgG4=", "admin", Constants.ADMIN_ROLE);
        schoolUserRepository.save(admin);

        // haslo: student
        SchoolUser student = new SchoolUser("student", "student", "$100801$CzaxAyZkwycp18sGzcZE33ymaEBuqHY579JJ8CzRdckDIUMYQADzXGPRE2Hqz3iZauxyIkkSbo3998KrBYVznA==$vbVS5qCtrKps3saxR7pmK+pA+TNiZQfNWwrcHS7qHuo=", "student", Constants.STUDENT_ROLE);
        SchoolUser student2 = new SchoolUser("student2", "student2", "$100801$CzaxAyZkwycp18sGzcZE33ymaEBuqHY579JJ8CzRdckDIUMYQADzXGPRE2Hqz3iZauxyIkkSbo3998KrBYVznA==$vbVS5qCtrKps3saxR7pmK+pA+TNiZQfNWwrcHS7qHuo=", "student2", Constants.STUDENT_ROLE);
        schoolUserRepository.save(student);
        schoolUserRepository.save(student2);

        // haslo: instructor
        SchoolUser instructor = new SchoolUser("instructor", "instructor", "$100801$mtcGeB1wJkCJufG6sWa/FJ110+v5R9nIhvFhccGm6IuTc9mA43NJQNQVz8Gbjy5XepW7tWaaI8QM7bpVDd0rmA==$gxqwRbGBy2s5ztOWgJRwfh2+TZJZvgfZCZSXHlQYE5k=", "instructor", Constants.INSTRUCTOR_ROLE);
        SchoolUser instructor2 = new SchoolUser("instructor2", "instructor2", "$100801$mtcGeB1wJkCJufG6sWa/FJ110+v5R9nIhvFhccGm6IuTc9mA43NJQNQVz8Gbjy5XepW7tWaaI8QM7bpVDd0rmA==$gxqwRbGBy2s5ztOWgJRwfh2+TZJZvgfZCZSXHlQYE5k=", "instructor2", Constants.INSTRUCTOR_ROLE);
        SchoolUser instructor3 = new SchoolUser("instructor3", "instructor3", "$100801$mtcGeB1wJkCJufG6sWa/FJ110+v5R9nIhvFhccGm6IuTc9mA43NJQNQVz8Gbjy5XepW7tWaaI8QM7bpVDd0rmA==$gxqwRbGBy2s5ztOWgJRwfh2+TZJZvgfZCZSXHlQYE5k=", "instructor3", Constants.INSTRUCTOR_ROLE);
        schoolUserRepository.save(instructor);
        schoolUserRepository.save(instructor2);
        schoolUserRepository.save(instructor3);

        studentInstructorRepository.save(new StudentInstructor(student, instructor, Constants.ACTIVE));
        studentInstructorRepository.save(new StudentInstructor(student, instructor2, Constants.ACTIVE));
        studentInstructorRepository.save(new StudentInstructor(student2, instructor, Constants.ACTIVE));
        studentInstructorRepository.save(new StudentInstructor(student2, instructor2, Constants.PENDING));
    }

    private void createLectures() {
        try {
            Lecture l = new Lecture("RUCH POJAZDOW", null, 1);
            Lecture l2 = new Lecture("RUCH POJAZDOW2", null, 2);
            Sublecture sl1 = new Sublecture("Ogólne zasady ruchu pojazdów.", "Kierującego pojazdem obowiązuje ruch prawostronny. Odwołując się do Kodeksu Ruchu Drogowego (rozdział 3, oddział 1, art. 16) „Kierujący pojazdem, korzystając z drogi dwujezdniowej, jest obowiązany jechać po prawej jezdni; do jezdni tych nie wlicza się jezdni przeznaczonej do dojazdu do nieruchomości położonej przy drodze”.", 1, l);
            Sublecture sl2 = new Sublecture("Włączanie się do ruchu.", "Włączanie się do ruchu wymaga zachowania szczególnej ostrożności, a także konieczności ustąpienia pierwszeństwa wszystkim pozostałym uczestnikom ruchu. Przede wszystkim, zanim rozpoczniesz ruch, dokonaj uważnej obserwacji sytuacji jaka występuje na drodze, a także podejmij trafną decyzję, czy manewr włączania się do ruchu przeprowadzisz w bezpieczny sposób. Jeżeli masz jakiekolwiek wątpliwości zaczekaj na poprawę sytuacji.", 2, l);
            List<Subject> subjects = new ArrayList<>(Arrays.asList(
                    new Subject(null, "Odwołując się do Kodeksu Ruchu Drogowego (rozdział 3, oddział 1, art. 16) „Kierujący pojazdem, korzystając z jezdni dwukierunkowej co najmniej o czterech pasach ruchu, jest obowiązany zajmować pas ruchu znajdujący się na prawej połowie jezdni”",
                            convertImage("/data/image/droga-dwujezdniowa.jpg"), 1, sl1),
                    new Subject(null,
                            "„Kierujący pojazdem jest obowiązany jechać możliwie blisko prawej krawędzi jezdni. Jeżeli pasy ruchu na jezdni są wyznaczone, nie może zajmować więcej niż jednego pasa.”",
                            convertImage("/data/image/zajmowanie-jednego-pasa-ruchu.jpg"), 2, sl1),
                    new Subject("Włączającym się do ruchu jesteś także w ściśle określonych przepisami sytuacjach, np. podczas:",
                            null, null, 3, sl2),
                    new Subject("1. wyjazdu ze strefy zamieszkania,",
                            null,
                            convertImage("/data/image/wyjazd-ze-strefy-zamieszkania.jpg"), 4, sl2),
                    new Subject("2. wyjazdu na drogę z nieruchomości, obiektu przydrożnego tj. parkingu, stacji benzynowej,",
                            null,
                            convertImage("/data/image/wyjazd-z-parkingu.jpg"), 5, sl2),
                    new Subject("3. wyjazdu z drogi niebędącej drogą publiczną,",
                            null,
                            convertImage("/data/image/wyjazd-z-drogi-niebedacej-droga-publiczna.jpg"), 6, sl2),
                    new Subject("4. wyjazdu na drogę z pola,",
                            null,
                            convertImage("/data/image/wyjazd-z-drogi-niebedacej-droga-publiczna.jpg"), 7, sl2),
                    new Subject("5. wyjazdu z drogi niebędącej drogą publiczną (drogi wewnętrznej),",
                            null,
                            convertImage("/data/image/wyjazd-z-drogi-wewnetrznej.jpg"), 8, sl2),
                    new Subject("6. wyjazdu z drogi dla rowerów na jezdnię lub pobocze, z wyjątkiem wjazdu na przejazd dla rowerzystów lub pas ruchu dla rowerów.",
                            null,
                            convertImage("/data/image/wyjazd-z-drogi-dla-rowerow.jpg"), 9, sl2)
            ));
            lectureRepository.save(l);
            lectureRepository.save(l2);
            sublectureRepository.saveAll(List.of(sl1, sl2));
            subjectRepository.saveAll(subjects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void mapQuestionsToDb() {
        List<Test> tests = testService.getAllTestsByCategory("B");
        try (CSVReader reader = new CSVReader(new FileReader("src/main/resources/data/questions/questions.csv"))) {
            List<String[]> records = reader.readAll();
            Integer iteration = 0;
            long startTime = System.nanoTime();
            for (String[] record : records) {
                Question question = new Question();
                question.setQuestion(record[0]);
                question.setAnswerA(record[1]);
                question.setAnswerB(record[2]);
                question.setAnswerC(record[3]);
                if (record[3].equals("BRAK"))
                    question.setAvailableAnswers(2L);
                else
                    question.setAvailableAnswers(3L);
                question.setCorrectAnswer(record[4].toUpperCase());
                question.setDrivingCategory(record[5]);
                question.setMediaName(record[6]);
                question.setQuestionType(record[7].equals("tak"));
                tests.forEach(t -> {
                    if (t.getName().equals(record[8]) && t.getTestType() == question.getQuestionType() && question.getDrivingCategory().contains(t.getDrivingCategory())) {
                        List<Test> questionTests = question.getTests();
                        questionTests.add(t);
                        question.setTests(questionTests);
                        questionService.save(question);
                        t.setNumberQuestions(t.getNumberQuestions() + 1);
                        testService.saveTest(t);
                    }
                });

                if (++iteration == 500)
                    break;
            }

            long durationNano = System.nanoTime() - startTime;
            long durationMillis = durationNano / 1_000_000;
            long durationSeconds = durationMillis / 1000;
            long minutes = durationSeconds / 60;
            long seconds = durationSeconds % 60;

            logger.info("Zmapowano Pytania w czasie: " + minutes + "minut " + seconds + "sekund");

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public void addTeststoDb() {
        for (String name : TestNames.getNames())
            addTestToDb(name, false);

        for (String name : TestNames.getSpecialTestNames())
            addTestToDb(name, true);
    }

    public void addTestToDb(String name, Boolean isSpecialistic) {
        Test test = new Test();
        test.setName(name);
        test.setDrivingCategory("B");
        test.setTestType(isSpecialistic);
        try {
            test.setImage(convertSvg("/data/testImageSvgs/" + name + ".svg"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        test.setNumberQuestions(0);
        testService.saveTest(test);
    }

    private void createEvents() {
        SchoolUser schoolUser = schoolUserRepository.findByEmail("instructor");
        SchoolUser schoolUser2 = schoolUserRepository.findByEmail("instructor2");
        SchoolUser schoolUser3 = schoolUserRepository.findByEmail("instructor3");
        SchoolUser student = schoolUserRepository.findByEmail("student");
        InstructionEvent ie = new InstructionEvent("Podstawowe zasady jazdy i wprowadzenie do ruchu drogowego", Constants.PRACTICAL_DRIVING_ON_ROADS, LocalDateTime.of(2024, 5, 1, 10, 0), LocalDateTime.of(2024, 5, 1, 12, 0), schoolUser, 12);
        ie.setStudents(Arrays.asList(student));
        ie.setAvailableEventSlots(11);

        List<InstructionEvent> events = Arrays.asList(
                ie,
                new InstructionEvent("Teoria jazdy: Zasady, przepisy i najlepsze praktyki", Constants.THEORY_OF_DRIVING_CLASSES, LocalDateTime.of(2024, 5, 2, 14, 0), LocalDateTime.of(2024, 5, 2, 16, 0), schoolUser, 10),
                new InstructionEvent("Ćwiczenia praktyczne: Bezpieczne poruszanie się po drogach", Constants.PRACTICAL_DRIVING_ON_ROADS, LocalDateTime.of(2024, 5, 3, 9, 0), LocalDateTime.of(2024, 5, 3, 11, 0), schoolUser, 3),
                new InstructionEvent("Spotkanie informacyjne: Wprowadzenie do kursu jazdy", Constants.INFORMATION_SESSIONS, LocalDateTime.of(2024, 5, 4, 15, 0), LocalDateTime.of(2024, 5, 4, 17, 0), schoolUser, 100),
                new InstructionEvent("Trening manewrów: Parkowanie, cofanie i zawracanie", Constants.BASIC_MANEUVERS, LocalDateTime.of(2024, 5, 5, 8, 0), LocalDateTime.of(2024, 5, 5, 10, 0), schoolUser, 54),
                new InstructionEvent("Kurs pierwszej pomocy: Reagowanie na wypadki drogowe", Constants.FIRST_AID_CLASSES, LocalDateTime.of(2024, 5, 6, 13, 0), LocalDateTime.of(2024, 5, 6, 15, 0), schoolUser, 65),
                new InstructionEvent("Jazda miejska: Radzenie sobie z ruchem ulicznym", Constants.CITY_DRIVING, LocalDateTime.of(2024, 5, 7, 11, 0), LocalDateTime.of(2024, 5, 7, 13, 0), schoolUser, 12),
                new InstructionEvent("Zaawansowane techniki jazdy: Skuteczność i bezpieczeństwo", Constants.PRACTICAL_DRIVING_ON_ROADS, LocalDateTime.of(2024, 5, 8, 12, 0), LocalDateTime.of(2024, 5, 8, 14, 0), schoolUser, 54),
                new InstructionEvent("Dogłębna analiza przepisów ruchu drogowego", Constants.THEORY_OF_DRIVING_CLASSES, LocalDateTime.of(2024, 5, 9, 16, 0), LocalDateTime.of(2024, 5, 9, 18, 0), schoolUser, 34),
                new InstructionEvent("Praktyczna nauka jazdy: Techniki zaawansowane", Constants.PRACTICAL_DRIVING_ON_ROADS, LocalDateTime.of(2024, 5, 10, 14, 0), LocalDateTime.of(2024, 5, 10, 16, 0), schoolUser, 34),
                new InstructionEvent("Aktualizacja przepisów drogowych: Co nowego w 2024?", Constants.LECTURES, LocalDateTime.of(2024, 5, 11, 10, 0), LocalDateTime.of(2024, 5, 11, 12, 0), schoolUser, 34),
                new InstructionEvent("Nocna jazda: Jak bezpiecznie poruszać się po zmroku", Constants.NIGHT_DRIVING, LocalDateTime.of(2024, 5, 12, 14, 0), LocalDateTime.of(2024, 5, 12, 16, 0), schoolUser, 34),
                new InstructionEvent("Jazda ekonomiczna: Techniki oszczędzania paliwa", Constants.ECO_DRIVING_CLASSES, LocalDateTime.of(2024, 5, 13, 9, 0), LocalDateTime.of(2024, 5, 13, 11, 0), schoolUser, 54),
                new InstructionEvent("Dodatkowe lekcje: Przygotowanie do egzaminu praktycznego", Constants.ADDITIONAL_DRIVING_LESSONS, LocalDateTime.of(2024, 5, 14, 15, 0), LocalDateTime.of(2024, 5, 14, 17, 0), schoolUser, 12),
                new InstructionEvent("Intensywny trening na placu manewrowym: Doskonalenie umiejętności", Constants.PRACTICAL_CLASSES_ON_MANEUVERING_GROUND, LocalDateTime.of(2024, 5, 15, 8, 0), LocalDateTime.of(2024, 5, 15, 10, 0), schoolUser, 56),
                new InstructionEvent("Warsztaty z technik awaryjnych: Jak reagować w sytuacjach kryzysowych", Constants.EMERGENCY_TECHNIQUES_TRAINING, LocalDateTime.of(2024, 5, 16, 13, 0), LocalDateTime.of(2024, 5, 16, 15, 0), schoolUser, 43),
                new InstructionEvent("Jazda poza miastem: Techniki jazdy na trasach szybkiego ruchu", Constants.OUT_OF_TOWN_DRIVING, LocalDateTime.of(2024, 5, 17, 11, 0), LocalDateTime.of(2024, 5, 17, 13, 0), schoolUser, 54),
                new InstructionEvent("Podstawy obsługi technicznej pojazdu: Co każdy kierowca powinien wiedzieć", Constants.CAR_MAINTENANCE_TRAINING, LocalDateTime.of(2024, 5, 18, 12, 0), LocalDateTime.of(2024, 5, 18, 14, 0), schoolUser, 23),
                new InstructionEvent("Spotkanie organizacyjne: Planowanie harmonogramu kursu", Constants.INFORMATION_AND_ORGANIZATIONAL_MEETINGS, LocalDateTime.of(2024, 5, 19, 16, 0), LocalDateTime.of(2024, 5, 19, 18, 0), schoolUser, 67),
                new InstructionEvent("Konsultacje z egzaminatorem: Przygotowanie do testów", Constants.MEETINGS_WITH_EXAMINERS, LocalDateTime.of(2024, 5, 20, 14, 0), LocalDateTime.of(2024, 5, 20, 16, 0), schoolUser, 67),
                new InstructionEvent("Konsultacje z egzaminatorem: Przygotowanie do testów", Constants.MEETINGS_WITH_EXAMINERS, LocalDateTime.of(2024, 6, 20, 14, 0), LocalDateTime.of(2024, 6, 20, 16, 0), schoolUser2, 54),
                new InstructionEvent("Driftowanie bokiem", Constants.MEETINGS_WITH_EXAMINERS, LocalDateTime.of(2024, 5, 20, 14, 0), LocalDateTime.of(2024, 5, 20, 16, 0), schoolUser3, 87)
        );

        eventRepository.saveAll(events);
    }

    public static byte[] convertImage(String resourcePath) throws Exception {
        ClassPathResource imgFile = new ClassPathResource(resourcePath);
        BufferedImage bImage = ImageIO.read(imgFile.getInputStream());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos);
        return bos.toByteArray();
    }

    public static byte[] convertSvg(String resourcePath) throws Exception {
        ClassPathResource svgFile = new ClassPathResource(resourcePath);
        try (InputStream inputStream = svgFile.getInputStream();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
                bos.write(buffer, 0, bytesRead);
            return bos.toByteArray();
        }
    }
}
