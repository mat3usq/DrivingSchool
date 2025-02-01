package com.driving.school.utils;

import com.driving.school.model.*;
import com.driving.school.repository.*;
import com.driving.school.service.*;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
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
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DataLoader implements CommandLineRunner {
    private final List<SchoolUser> schoolUsers = new LinkedList<>();
    private final List<Category> categories = new LinkedList<>();
    private final List<MentorShip> mentorShips = new LinkedList<>();
    private final QuestionRepository questionRepository;
    private final List<Test> tests = new LinkedList<>();
    private final List<Payment> payments = new LinkedList<>();
    private final List<Course> courses = new LinkedList<>();
    private final List<Question> questions = new LinkedList<>();
    private final List<Lecture> lectures = new LinkedList<>();
    private final List<Sublecture> sublectures = new LinkedList<>();
    private final List<Subject> subjects = new LinkedList<>();
    private final List<InstructionEvent> instructionEvents = new LinkedList<>();
    private final List<Mail> mails = new LinkedList<>();
    private final Set<StudentAnswersTest> studentAnswersTests = new HashSet<>();
    private final List<StudentExam> studentExams = new LinkedList<>();

    private final SchoolUserRepository schoolUserRepository;
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    private final CategoryRepository categoryRepository;
    private final MentorShipRepository mentorShipRepository;
    private final TestRepository testRepository;
    private final SchoolUserService schoolUserService;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;
    private final SublectureRepository sublectureRepository;
    private final SubjectRepository subjectRepository;
    private final InstructionEventRepository instructionEventRepository;
    private final MailService mailService;
    private final TestService testService;
    private final StudentAnswersTestRepository studentAnswersTestRepository;
    private final TestStatisticsService testStatisticsService;
    private final StudentExamService studentExamService;
    private final StudentExamAnswerRepository studentExamAnswerRepository;
    private final StudentExamRepository studentExamRepository;

    @Autowired
    public DataLoader(SchoolUserRepository schoolUserRepository, CategoryRepository categoryRepository, MentorShipRepository mentorShipRepository, TestRepository testRepository, SchoolUserService schoolUserService, CourseRepository courseRepository, QuestionRepository questionRepository, LectureRepository lectureRepository, SublectureRepository sublectureRepository, SubjectRepository subjectRepository, InstructionEventRepository instructionEventRepository, MailService mailService, TestService testService, StudentAnswersTestRepository studentAnswersTestRepository, TestStatisticsService testStatisticsService, StudentExamService studentExamService, StudentExamAnswerRepository studentExamAnswerRepository, StudentExamRepository studentExamRepository) {
        this.schoolUserRepository = schoolUserRepository;
        this.categoryRepository = categoryRepository;
        this.mentorShipRepository = mentorShipRepository;
        this.testRepository = testRepository;
        this.schoolUserService = schoolUserService;
        this.courseRepository = courseRepository;
        this.questionRepository = questionRepository;
        this.lectureRepository = lectureRepository;
        this.sublectureRepository = sublectureRepository;
        this.subjectRepository = subjectRepository;
        this.instructionEventRepository = instructionEventRepository;
        this.mailService = mailService;
        this.testService = testService;
        this.studentAnswersTestRepository = studentAnswersTestRepository;
        this.testStatisticsService = testStatisticsService;
        this.studentExamService = studentExamService;
        this.studentExamAnswerRepository = studentExamAnswerRepository;
        this.studentExamRepository = studentExamRepository;
    }

    @Override
    public void run(String... args) {
        if (schoolUserRepository.findByEmail("jan.kowalski@gmail.com") != null) {
            logger.info("\u001B[1mAll \u001B[1;34mdata\u001B[1;39m was loaded in \u001B[1;33mpast\u001B[1;39m to database.\u001B[0m");
            return;
        }

        long startTime = System.nanoTime();
        loadUsers();
        loadCategories();
        loadPayments();
        loadMentorships();
        loadTests();
        loadQuestions();
        loadCourses();
        loadLectures();
        loadInstructionEvents();
        loadMails();
        loadDoneQuestionsInTestsByUser();
        loadDoneExamsByUser();
        logger.info("\u001B[1mAll \u001B[1;34mdata\u001B[1;39m loaded in \u001B[1;33m{}\u001B[1;39m to database.\u001B[0m", formatDuration(System.nanoTime() - startTime));
    }

    private void loadUsers() {
        long startTime = System.nanoTime();

        // password: admin
        // categories: all
        SchoolUser admin = new SchoolUser("Jan", "Kowalski", "$100801$cfJJlxSl83FjJ2mh+6yUcdxxksVm3XlOzhBr4gLHFEOhdeWmaf2H6Lki/fe99YUMduDoX/LGHUcodWe9SkhVnw==$Q1yyulzoYXseBJ/OmM/xgcYD9fFPaw2bRzBHRW7RgG4=", "jan.kowalski@gmail.com", Constants.ADMIN_ROLE, "B");

        // password: student
        // categories: "A,B,D"
        SchoolUser student = new SchoolUser("Anna", "Nowak", "$100801$CzaxAyZkwycp18sGzcZE33ymaEBuqHY579JJ8CzRdckDIUMYQADzXGPRE2Hqz3iZauxyIkkSbo3998KrBYVznA==$vbVS5qCtrKps3saxR7pmK+pA+TNiZQfNWwrcHS7qHuo=", "anna.nowak123@gmail.com", Constants.STUDENT_ROLE, "");
        // categories: "D, PT"
        SchoolUser student2 = new SchoolUser("Michał", "Szczepura", "$100801$CzaxAyZkwycp18sGzcZE33ymaEBuqHY579JJ8CzRdckDIUMYQADzXGPRE2Hqz3iZauxyIkkSbo3998KrBYVznA==$vbVS5qCtrKps3saxR7pmK+pA+TNiZQfNWwrcHS7qHuo=", "michal.szczepura@yahoo.com", Constants.STUDENT_ROLE, "");

        // password: instructor
        // categories: all
        SchoolUser instructor = new SchoolUser("Krzysztof", "Wiśniewski", "$100801$mtcGeB1wJkCJufG6sWa/FJ110+v5R9nIhvFhccGm6IuTc9mA43NJQNQVz8Gbjy5XepW7tWaaI8QM7bpVDd0rmA==$gxqwRbGBy2s5ztOWgJRwfh2+TZJZvgfZCZSXHlQYE5k=", "krzysztof.wisniewski@outlook.com", Constants.INSTRUCTOR_ROLE, "");
        SchoolUser instructor2 = new SchoolUser("Ewa", "Kowalczyk", "$100801$mtcGeB1wJkCJufG6sWa/FJ110+v5R9nIhvFhccGm6IuTc9mA43NJQNQVz8Gbjy5XepW7tWaaI8QM7bpVDd0rmA==$gxqwRbGBy2s5ztOWgJRwfh2+TZJZvgfZCZSXHlQYE5k=", "ewa.kowalczyk@wp.pl", Constants.INSTRUCTOR_ROLE, "");
        SchoolUser instructor3 = new SchoolUser("Piotr", "Mazur", "$100801$mtcGeB1wJkCJufG6sWa/FJ110+v5R9nIhvFhccGm6IuTc9mA43NJQNQVz8Gbjy5XepW7tWaaI8QM7bpVDd0rmA==$gxqwRbGBy2s5ztOWgJRwfh2+TZJZvgfZCZSXHlQYE5k=", "piotr.mazur@gmail.com", Constants.INSTRUCTOR_ROLE, "");

        schoolUsers.add(admin);
        schoolUsers.add(student);
        schoolUsers.add(student2);
        schoolUsers.add(instructor);
        schoolUsers.add(instructor2);
        schoolUsers.add(instructor3);

        schoolUserRepository.saveAll(schoolUsers);

        logger.info("Loaded {} users in {} to database.", schoolUsers.size(), formatDuration(System.nanoTime() - startTime));
    }

    private void loadPayments() {
        long startTime = System.nanoTime();

        payments.add(new Payment(0.0, "Dostęp do wszystkich kategorii prawa jazdy ze względu na uprawnienia konta.", categories, schoolUsers.get(0)));
        payments.add(new Payment(125.5, "Opłata za kategorie na motor, samochód osobowy oraz ciężarowy.", Arrays.asList(categories.get(0), categories.get(1), categories.get(3)), schoolUsers.get(1)));
        payments.add(new Payment(100.0, "Opłata za kategorie na autobus oraz tramwaj.", Arrays.asList(categories.get(3), categories.get(11)), schoolUsers.get(2)));
        payments.add(new Payment(0.0, "Dostęp do wszystkich kategorii prawa jazdy ze względu na uprawnienia konta.", categories, schoolUsers.get(3)));
        payments.add(new Payment(0.0, "Dostęp do wszystkich kategorii prawa jazdy ze względu na uprawnienia konta.", categories, schoolUsers.get(4)));
        payments.add(new Payment(0.0, "Dostęp do wszystkich kategorii prawa jazdy ze względu na uprawnienia konta.", categories, schoolUsers.get(5)));

        payments.forEach(p -> schoolUserService.addPayment(p.getSchoolUser().getId(), p));

        logger.info("Loaded {} payments in {} to database.", payments.size(), formatDuration(System.nanoTime() - startTime));
    }

    private void loadCategories() {
        long startTime = System.nanoTime();

        // categories: "A,B,C,D,T,AM,A1,A2,B1,C1,D1,PT"
        categories.add(new Category("A", "20 lat - jeśli masz już od co najmniej 2 lat prawo jazdy kategorii A2 /\\ 24 lata - jeśli nie masz od co najmniej 2 lat prawa jazdy kategorii A2",
                Arrays.asList("każdym motocyklem,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM,",
                        "zespołem pojazdów złożonym z pojazdu określonego dla tej kategorii wraz z przyczepą – tylko w Polsce.")));
        categories.add(new Category("B", "18 lat",
                Arrays.asList("pojazdem samochodowym o dopuszczalnej masie całkowitej nieprzekraczającej 3,5 t, z wyjątkiem autobusu i motocykla,",
                        "zespołem pojazdów złożonym z pojazdu, o którym mowa wyżej oraz z przyczepy lekkiej,",
                        "zespołem pojazdów samochodowych o dopuszczalnej masie całkowitej nieprzekraczającej 3,5 t oraz z przyczepy innej niż lekka, o ile łączna dopuszczalna masa całkowita zespołu tych pojazdów nie przekracza 4250 kg,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM,",
                        "ciągnikiem rolniczym, pojazdem wolnobieżnym oraz zespołem złożonym z tego pojazdu i przyczepy lekkiej – tylko w Polsce,",
                        "motocyklem o pojemności skokowej silnika nieprzekraczającej 125 cm3, mocy nieprzekraczającej 11 kW i stosunku mocy do masy własnej nieprzekraczającym 0,1 kW/kg, lub motocyklem trójkołowym, pod warunkiem że osoba posiada prawo jazdy kategorii B od co najmniej 3 lat,",
                        "pojazdem samochodowym zasilanym paliwami alternatywnymi o dopuszczalnej masie całkowitej przekraczającej 3,5 t oraz nieprzekraczającej 4250 kg, jeżeli przekroczenie dopuszczalnej masy całkowitej 3,5 t wynika z zastosowania paliw alternatywnych, o których mowa w przepisach wydanych na podstawie art. 66 ust. 5 ustawy z dnia 20 czerwca 1997 r. - Prawo o ruchu drogowym, a informacja o tym przekroczeniu jest odnotowana w dowodzie rejestracyjnym, pod warunkiem że osoba posiada prawo jazdy kategorii B od co najmniej 2 lat.")));
        categories.add(new Category("C", "21 lat",
                Arrays.asList("pojazdem samochodowym o dopuszczalnej masie całkowitej przekraczającej 3,5 t, z wyjątkiem autobusu,",
                        "zespołem pojazdów złożonym z pojazdu, o którym mowa wyżej oraz z przyczepy lekkiej,",
                        "ciągnikiem rolniczym, pojazdem wolnobieżnym oraz zespołem złożonym z tego pojazdu i przyczepy lekkiej – tylko w Polsce,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM.")));
        categories.add(new Category("D", "24 lata",
                Arrays.asList("autobusem,",
                        "zespołem pojazdów złożonym z pojazdu, o którym wyżej oraz z przyczepy lekkiej,",
                        "ciągnikiem rolniczym, pojazdem wolnobieżnym oraz zespołem złożonym z tego pojazdu i przyczepy lekkiej – tylko w Polsce,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM.")));
        categories.add(new Category("T", "16 lat",
                Arrays.asList("ciągnikiem rolniczym lub pojazdem wolnobieżnym,",
                        "zespołem pojazdów złożonym z ciągnika rolniczego z przyczepą (przyczepami) lub pojazdem wolnobieżnym z przyczepą (przyczepami),",
                        "pojazdami określonymi dla prawa jazdy kategorii AM.")));
        categories.add(new Category("AM", "14 lat",
                Arrays.asList("motorowerem,", "czterokołowcem lekkim (tzw. małym quadem),", "zespołem pojazdów złożonym z pojazdu określonego dla tej kategorii wraz z przyczepą – tylko w Polsce.")));
        categories.add(new Category("A1", "16 lat",
                Arrays.asList("motocyklem o pojemności skokowej silnika nieprzekraczającej 125 cm3, mocy nieprzekraczającej 11 kW i stosunku mocy do masy własnej nieprzekraczającym 0,1 kW/kg,",
                        "motocyklem trójkołowym o mocy nieprzekraczającej 15 kW,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM,",
                        "zespołem pojazdów złożonym z pojazdu określonego dla tej kategorii wraz z przyczepą – tylko w Polsce.")));
        categories.add(new Category("A2", "18 lat",
                Arrays.asList("motocyklem o mocy nieprzekraczającej 35 kW i stosunku mocy do masy własnej nieprzekraczającym 0,2 kW/kg, przy czym nie może on powstać w wyniku wprowadzenia zmian w pojeździe o mocy przekraczającej dwukrotność mocy tego motocykla,",
                        "motocyklem trójkołowym o mocy nieprzekraczającej 15 kW,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM,",
                        "zespołem pojazdów złożonym z pojazdu określonego dla tej kategorii wraz z przyczepą – tylko w Polsce.")));
        categories.add(new Category("B1", "16 lat",
                Arrays.asList("czterokołowcem,", "pojazdami określonymi dla prawa jazdy kategorii AM.")));
        categories.add(new Category("C1", "18 lat",
                Arrays.asList("pojazdem samochodowym o dopuszczalnej masie całkowitej przekraczającej 3,5 t i nieprzekraczającej 7,5 t, z wyjątkiem autobusu,",
                        "zespołem pojazdów złożonym z pojazdu, o którym mowa wyżej oraz z przyczepy lekkiej,",
                        "ciągnikiem rolniczym, pojazdem wolnobieżnym oraz zespołem złożonym z tego pojazdu i przyczepy lekkiej – tylko w Polsce,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM.")));
        categories.add(new Category("D1", "21 lat",
                Arrays.asList("autobusem przeznaczonym konstrukcyjnie do przewozu nie więcej niż 17 osób łącznie z kierowcą, o długości nieprzekraczającej 8 m,",
                        "zespołem pojazdów złożonym z pojazdu, o którym mowa wyżej oraz z przyczepy lekkiej,",
                        "ciągnikiem rolniczym, pojazdem wolnobieżnym oraz zespołem złożonym z tego pojazdu i przyczepy lekkiej – tylko w Polsce,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM.")));
        categories.add(new Category("PT", "21 lat",
                Arrays.asList("tramwaj,",
                        "ciągnik rolniczy i pojazd wolnobieżny,",
                        "zespół pojazdów złożony z ciągnika rolniczego lub pojazdu wolnobieżnego oraz jednej i więcej przyczep,",
                        "pojazd kategorii AM.")));

        categoryRepository.saveAll(categories);

        logger.info("Loaded {} categories in {} to database.", categories.size(), formatDuration(System.nanoTime() - startTime));
    }

    private void loadMentorships() {
        long startTime = System.nanoTime();

        mentorShips.add(new MentorShip(schoolUsers.get(1), schoolUsers.get(3), Constants.ACTIVE));
        mentorShips.add(new MentorShip(schoolUsers.get(1), schoolUsers.get(3), Constants.COMPLETED));
        mentorShips.add(new MentorShip(schoolUsers.get(1), schoolUsers.get(4), Constants.ACTIVE));
        mentorShips.add(new MentorShip(schoolUsers.get(2), schoolUsers.get(3), Constants.ACTIVE));
        mentorShips.add(new MentorShip(schoolUsers.get(2), schoolUsers.get(4), Constants.PENDING));

        mentorShipRepository.saveAll(mentorShips);

        logger.info("Loaded {} mentorships in {} to database.", mentorShips.size(), formatDuration(System.nanoTime() - startTime));
    }

    private void loadTests() {
        long startTime = System.nanoTime();

        categories.forEach(category -> {
            for (String name : TestNames.getNames())
                tests.add(addTestToDb(name, false, category.getNameCategory()));

            for (String name : TestNames.getSpecialTestNames())
                tests.add(addTestToDb(name, true, category.getNameCategory()));
        });

        testRepository.saveAll(tests);

        logger.info("Loaded {} tests in {} to database.", tests.size(), formatDuration(System.nanoTime() - startTime));
    }

    private void loadCourses() {
        long startTime = System.nanoTime();

        Course firstCourse = new Course("Kurs na prawo jazdy, mający na celu doskonalenie umiejętności technicznych oraz zwiększenie doświadczenia praktycznego na motocyklu.",
                categories.get(0), 120.0, 6.5, 66.33, mentorShips.get(0), Constants.COURSE_NOTSPECIFIED);
        firstCourse.setDrivingSessions(List.of(new DrivingSession(LocalDateTime.now(), 2.0, "Pierwsza sesja - ćwiczenia manewrowe na placu manewrowym.", firstCourse),
                new DrivingSession(LocalDateTime.now(), 1.5, "Druga sesja - poruszanie się w ruchu okrężnym na rondzie.", firstCourse),
                new DrivingSession(LocalDateTime.now(), 3.0, "Trzecia sesja - przygotowania do egzaminu praktycznego.", firstCourse)));
        firstCourse.setTestCourses(List.of(new TestCourse(LocalDateTime.now(), "Wynik bardzo dobry - znaki ostrzegawcze.", Constants.COURSE_TEST_THEORETICAL, 96.5, firstCourse),
                new TestCourse(LocalDateTime.now(), "Wynik rewelacyjny - przepisy ruchu drogowego.", Constants.COURSE_TEST_GENERAL, 86.5, firstCourse),
                new TestCourse(LocalDateTime.now(), "Egzamin praktyczny zakończony na placu manewrowym.", Constants.COURSE_TEST_PRACTICAL, 16.0, firstCourse)));
        firstCourse.setCommentCourses(List.of(new CommentCourse(LocalDateTime.now(), "Proszę pamiętać o przygotowaniu swojego stanowiska pracy przed rozpoczęciem kursu.", firstCourse),
                new CommentCourse(LocalDateTime.now(), "Zaleca się delikatniejsze używanie gazu podczas ruszania oraz płynne puszczanie sprzęgła.", firstCourse),
                new CommentCourse(LocalDateTime.now(), "Podczas przejazdu przez przejścia dla pieszych należy zachować szczególną ostrożność.", firstCourse)));

        Course secondCourse = new Course("Kurs na prawo jazdy kategorii B, mający na celu przygotowanie uczestników do bezpiecznego i efektywnego poruszania się pojazdami osobowymi.",
                categories.get(1), 30.0, 17.5, 87.86, mentorShips.get(0), Constants.COURSE_PASSED);
        secondCourse.setDrivingSessions(List.of(new DrivingSession(LocalDateTime.now().minusDays(14), 3.0, "Sesja 1 - Nauka podstawowych manewrów na placu manewrowym.", secondCourse),
                new DrivingSession(LocalDateTime.now().minusDays(12), 2.5, "Sesja 2 - Jazda w ruchu miejskim.", secondCourse),
                new DrivingSession(LocalDateTime.now().minusDays(10), 4.0, "Sesja 3 - Parkowanie równoległe i prostopadłe.", secondCourse),
                new DrivingSession(LocalDateTime.now().minusDays(7), 3.5, "Sesja 4 - Jazda w ruchu autostradowym.", secondCourse),
                new DrivingSession(LocalDateTime.now().minusDays(5), 4.5, "Sesja 5 - Przygotowanie do egzaminu praktycznego.", secondCourse)));
        secondCourse.setTestCourses(List.of(new TestCourse(LocalDateTime.now().minusDays(9), "Wynik bardzo dobry - teoria ruchu drogowego.", Constants.COURSE_TEST_THEORETICAL, 92.0, secondCourse),
                new TestCourse(LocalDateTime.now().minusDays(7), "Wynik świetny - zasady bezpiecznej jazdy.", Constants.COURSE_TEST_GENERAL, 89.5, secondCourse),
                new TestCourse(LocalDateTime.now().minusDays(4), "Egzamin praktyczny zaliczony z wyróżnieniem.", Constants.COURSE_TEST_PRACTICAL, 76.0, secondCourse),
                new TestCourse(LocalDateTime.now().minusDays(2), "Dodatkowy test praktyczny - poprawa technik manewrowych.", Constants.COURSE_TEST_PRACTICAL, 85.0, secondCourse)));
        secondCourse.setCommentCourses(List.of(new CommentCourse(LocalDateTime.now().minusDays(14), "Proszę pamiętać o regularnym sprawdzaniu stanu technicznego pojazdu.", secondCourse),
                new CommentCourse(LocalDateTime.now().minusDays(10), "Podczas jazdy w ruchu miejskim zwrócić uwagę na pieszych i rowerzystów.", secondCourse),
                new CommentCourse(LocalDateTime.now().minusDays(7), "Przed każdą sesją upewnij się, że masz wygodne obuwie i odpowiednią odzież.", secondCourse),
                new CommentCourse(LocalDateTime.now().minusDays(5), "Regularne notowanie swoich postępów pomoże w lepszym przygotowaniu do egzaminu.", secondCourse),
                new CommentCourse(LocalDateTime.now(), "Ćwicz manewrowanie w ciasnych przestrzeniach, aby zwiększyć pewność siebie na drodze.", secondCourse)));

        Course thirdCourse = new Course("Kurs na prawo jazdy kategorii tramwajowej, mający na celu przygotowanie uczestników do bezpiecznego i efektywnego prowadzenia tramwaju w różnych warunkach miejskich.",
                categories.get(11), 40.0, 20.0, 90.25, mentorShips.get(1), Constants.COURSE_PASSED);
        thirdCourse.setDrivingSessions(List.of(new DrivingSession(LocalDateTime.now().minusDays(20), 4.0, "Sesja 1 - Wprowadzenie do obsługi tramwaju i podstawowe manewry.", thirdCourse),
                new DrivingSession(LocalDateTime.now().minusDays(18), 3.5, "Sesja 2 - Jazda w ruchu miejskim i zarządzanie pasażerami.", thirdCourse),
                new DrivingSession(LocalDateTime.now().minusDays(15), 4.5, "Sesja 3 - Obsługa sygnalizacji tramwajowej i interakcja z innymi pojazdami.", thirdCourse),
                new DrivingSession(LocalDateTime.now().minusDays(12), 5.0, "Sesja 4 - Jazda nocna i w trudnych warunkach atmosferycznych.", thirdCourse),
                new DrivingSession(LocalDateTime.now().minusDays(10), 5.5, "Sesja 5 - Przygotowanie do egzaminu praktycznego i symulacje rzeczywistych sytuacji drogowych.", thirdCourse)));
        thirdCourse.setTestCourses(List.of(new TestCourse(LocalDateTime.now().minusDays(17), "Wynik bardzo dobry - teoria prowadzenia tramwaju.", Constants.COURSE_TEST_THEORETICAL, 95.0, thirdCourse),
                new TestCourse(LocalDateTime.now().minusDays(14), "Wynik świetny - zasady bezpieczeństwa i zarządzanie pasażerami.", Constants.COURSE_TEST_GENERAL, 93.5, thirdCourse),
                new TestCourse(LocalDateTime.now().minusDays(11), "Egzamin praktyczny zaliczony z wyróżnieniem.", Constants.COURSE_TEST_PRACTICAL, 30.0, thirdCourse),
                new TestCourse(LocalDateTime.now().minusDays(8), "Dodatkowy test praktyczny - doskonalenie technik jazdy w zatłoczonych obszarach.", Constants.COURSE_TEST_PRACTICAL, 25.0, thirdCourse)));
        thirdCourse.setCommentCourses(List.of(new CommentCourse(LocalDateTime.now().minusDays(20), "Proszę o zapoznanie się z instrukcją obsługi tramwaju przed rozpoczęciem kursu.", thirdCourse),
                new CommentCourse(LocalDateTime.now().minusDays(18), "Zaleca się punktualność i przygotowanie odpowiedniego ubioru na sesje praktyczne.", thirdCourse),
                new CommentCourse(LocalDateTime.now().minusDays(15), "Podczas jazdy z pasażerami należy szczególnie dbać o komfort i bezpieczeństwo podróży.", thirdCourse),
                new CommentCourse(LocalDateTime.now().minusDays(12), "Regularne ćwiczenia manewrów pomogą w budowaniu pewności siebie za kierownicą.", thirdCourse),
                new CommentCourse(LocalDateTime.now(), "Zwracaj uwagę na sygnalizację tramwajową i przestrzegaj zasad ruchu tramwajowego.", thirdCourse)));

        Course fourthCourse = new Course("Kurs na prawo jazdy kategorii C, przeznaczony dla osób planujących prowadzenie pojazdów ciężarowych typu tir, z naciskiem na bezpieczną jazdę na długich trasach i zarządzanie czasem pracy.",
                categories.get(2), 50.0, 25.0, 88.75, mentorShips.get(1), Constants.COURSE_PASSED);
        fourthCourse.setDrivingSessions(List.of(new DrivingSession(LocalDateTime.now().minusDays(25), 5.0, "Sesja 1 - Wprowadzenie do obsługi tira i podstawowe manewry.", fourthCourse),
                new DrivingSession(LocalDateTime.now().minusDays(23), 4.5, "Sesja 2 - Jazda w ruchu miejskim i zarządzanie przestrzenią ładunkową.", fourthCourse),
                new DrivingSession(LocalDateTime.now().minusDays(20), 6.0, "Sesja 3 - Jazda na autostradzie i techniki zmiany pasa ruchu.", fourthCourse),
                new DrivingSession(LocalDateTime.now().minusDays(18), 5.5, "Sesja 4 - Jazda nocna i w trudnych warunkach pogodowych.", fourthCourse),
                new DrivingSession(LocalDateTime.now().minusDays(15), 6.5, "Sesja 5 - Zarządzanie czasem pracy i odpoczynku zgodnie z przepisami.", fourthCourse)));
        fourthCourse.setTestCourses(List.of(new TestCourse(LocalDateTime.now().minusDays(22), "Wynik bardzo dobry - teoria prowadzenia pojazdów ciężarowych.", Constants.COURSE_TEST_THEORETICAL, 90.0, fourthCourse),
                new TestCourse(LocalDateTime.now().minusDays(19), "Wynik świetny - zasady bezpiecznej jazdy i zarządzanie ładunkiem.", Constants.COURSE_TEST_GENERAL, 88.5, fourthCourse),
                new TestCourse(LocalDateTime.now().minusDays(16), "Egzamin praktyczny zaliczony z wyróżnieniem.", Constants.COURSE_TEST_PRACTICAL, 35.0, fourthCourse),
                new TestCourse(LocalDateTime.now().minusDays(13), "Dodatkowy test praktyczny - doskonalenie technik jazdy w trudnych warunkach.", Constants.COURSE_TEST_PRACTICAL, 30.0, fourthCourse)));
        fourthCourse.setCommentCourses(List.of(new CommentCourse(LocalDateTime.now().minusDays(25), "Proszę o zapoznanie się z dokumentacją techniczną tira przed rozpoczęciem kursu.", fourthCourse),
                new CommentCourse(LocalDateTime.now().minusDays(23), "Zaleca się regularne sprawdzanie stanu technicznego pojazdu, zwłaszcza układu hamulcowego.", fourthCourse),
                new CommentCourse(LocalDateTime.now().minusDays(20), "Podczas długich tras dbaj o regularne przerwy na odpoczynek zgodnie z przepisami.", fourthCourse),
                new CommentCourse(LocalDateTime.now().minusDays(18), "Regularne notowanie swoich postępów pomoże w lepszym przygotowaniu do egzaminu.", fourthCourse),
                new CommentCourse(LocalDateTime.now(), "Ćwicz manewrowanie w ciasnych przestrzeniach, aby zwiększyć pewność siebie na drodze.", fourthCourse)));

        courses.add(firstCourse);
        courses.add(secondCourse);
        courses.add(thirdCourse);
        courses.add(fourthCourse);
        courseRepository.saveAll(courses);

        logger.info("Loaded {} courses in {} to database.", courses.size(), formatDuration(System.nanoTime() - startTime));
    }

    private void loadQuestions() {
        long startTime = System.nanoTime();

        Map<String, List<Test>> testMapByNameAndType = tests.stream().collect(Collectors.groupingBy(t -> t.getName().toLowerCase() + "_" + t.getTestType()));
        List<Test> otherTests = testRepository.findByName("Inne");
        Map<Boolean, List<Test>> otherTestsByType = otherTests.stream().collect(Collectors.groupingBy(Test::getTestType));

        try (CSVReader reader = new CSVReaderBuilder(new FileReader("src/main/resources/data/questions/questions.csv"))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(';')
                        .build())
                .build()) {
            List<String[]> records = reader.readAll();
            Set<Test> testsToSave = new HashSet<>();

            for (String[] record : records) {
                Question question = new Question();

                question.setQuestion(record[0]);
                question.setMediaName(record[1]);
                question.setDrivingCategory(record[2]);
                question.setSubjectArea(record[3]);

                if (TestNames.getNames().stream().noneMatch(name -> name.equalsIgnoreCase(question.getSubjectArea())))
                    question.setSubjectArea("Inne");

                question.setExplanation(record[4]);
                question.setAnswerA(record[5]);
                question.setAnswerB(record[6]);
                question.setAnswerC(record[7]);
                question.setCorrectAnswer(record[8].toUpperCase());
                question.setPoints(Long.valueOf(record[9]));
                question.setSource(record[10]);
                question.setConnectionWithSecurity(record[11]);
                question.setQuestionType(record[12].equals("SPECJALISTYCZNY"));

                if (record[6].isEmpty())
                    question.setAvailableAnswers(2L);
                else
                    question.setAvailableAnswers(3L);

                if (question.getQuestionType())
                    question.setAllTimeForQuestion(50);
                else {
                    question.setTimeForPrepare(20);
                    question.setTimeForThink(15);
                    question.setAllTimeForQuestion(35);
                }

                String key = question.getSubjectArea().toLowerCase() + "_" + question.getQuestionType();
                List<Test> matchingTests = testMapByNameAndType.getOrDefault(key, new ArrayList<>());

                List<Test> testsToAdd = new ArrayList<>();

                if (!matchingTests.isEmpty()) {
                    for (Test t : matchingTests) {
                        if (question.getDrivingCategory().contains(t.getDrivingCategory())) {
                            testsToAdd.add(t);
                            t.setNumberQuestions(t.getNumberQuestions() + 1);
                            testsToSave.add(t);
                        }
                    }
                } else {
                    List<Test> otherMatchingTests = otherTestsByType.getOrDefault(question.getQuestionType(), new ArrayList<>());
                    for (Test ot : otherMatchingTests) {
                        if (question.getDrivingCategory().contains(ot.getDrivingCategory())) {
                            testsToAdd.add(ot);
                            ot.setNumberQuestions(ot.getNumberQuestions() + 1);
                            testsToSave.add(ot);
                        }
                    }
                }

                if (!testsToAdd.isEmpty()) {
                    question.setTests(testsToAdd);
                    questions.add(question);
                }
            }

            questionRepository.saveAll(questions);
            testRepository.saveAll(new ArrayList<>(testsToSave));

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        logger.info("Loaded {} questions in {} to database.", questions.size(), formatDuration(System.nanoTime() - startTime));
    }

    private void loadLectures() {
        long startTime = System.nanoTime();

        try {
            lectures.addAll(Arrays.asList(
                    new Lecture("Ruch Pojazdów", null, 1, "B")
            ));

            sublectures.addAll(Arrays.asList(
                    new Sublecture(
                            "Podstawowe zasady ruchu na drogach",
                            "Prawo drogowe zobowiązuje kierujących do jazdy przy prawej krawędzi jezdni. W przypadku dróg dwujezdniowych należy korzystać tylko z prawej jezdni, wykluczając pasy przeznaczone do dojazdu do nieruchomości zlokalizowanych przy drodze. Taka organizacja ruchu przekłada się na zwiększone bezpieczeństwo oraz klarowność sytuacji na drodze. Stosowanie się do tych reguł ogranicza ryzyko kolizji i wspiera przewidywalność zachowań innych kierowców.",
                            1,
                            lectures.get(0)
                    ),
                    new Sublecture(
                            "Ograniczenia Prędkości w Polsce",
                            null,
                            2,
                            lectures.get(0)
                    ),
                    new Sublecture(
                            "Autostrady",
                            null,
                            3,
                            lectures.get(0)
                    ),
                    new Sublecture(
                            "Drogi ekspresowe",
                            null,
                            4,
                            lectures.get(0)
                    )
            ));

            subjects.addAll(Arrays.asList(
                    new Subject(
                            null,
                            "Zgodnie z przepisami, na drodze dwukierunkowej o co najmniej czterech pasach ruchu należy zajmować jedynie pasy zlokalizowane po prawej połowie jezdni. Pozwala to zapobiegać kolizjom, ułatwia manewry wyprzedzania oraz zapewnia jasne zasady poruszania się innym uczestnikom ruchu.",
                            convertImage("/data/lectureImages/drogaDwujezdniowa.jpg"),
                            1,
                            sublectures.get(0)
                    ),
                    new Subject(
                            null,
                            "Prawo wymaga jazdy możliwie najbliżej prawej krawędzi i zabrania równoczesnego korzystania z więcej niż jednego pasa, gdy pasy ruchu zostały wyznaczone na jezdni.",
                            convertImage("/data/lectureImages/jazdaZPrawejStrony.jpg"),
                            2,
                            sublectures.get(0)
                    ),
                    new Subject(
                            null,
                            "Znak D-39, informujący o dopuszczalnych prędkościach w Polsce.",
                            convertImage("/data/lectureImages/ograniczeniaPredkosci.jpg"),
                            3,
                            sublectures.get(1)
                    ),
                    new Subject(
                            "Autostrada to dwujezdniowa droga oznaczona specjalnymi znakami, przeznaczona wyłącznie do ruchu pojazdów samochodowych (z pominięciem czterokołowców), zdolnych osiągnąć na płaskiej nawierzchni przynajmniej 40 km/h, także z przyczepą. Nie występuje na niej ruch poprzeczny.",
                            "Znak D-9, informujący o początku autostrady.",
                            convertImage("/data/lectureImages/poczatekAutostrady.png"),
                            4,
                            sublectures.get(2)
                    ),
                    new Subject(
                            null,
                            "Tabliczka T-28 informująca, że za przejazd daną drogą trzeba uiścić opłatę.",
                            convertImage("/data/lectureImages/platne.png"),
                            5,
                            sublectures.get(2)
                    ),
                    new Subject(
                            null,
                            "Znak D-10, sygnalizujący koniec autostrady.",
                            convertImage("/data/lectureImages/koniecAutostrady.png"),
                            6,
                            sublectures.get(2)
                    ),
                    new Subject(
                            "Na autostradzie zabronione jest:",
                            "- wykonywanie manewru zawracania,\n" +
                                    "- cofanie pojazdu,\n" +
                                    "- holowanie (chyba że odbywa się przy pomocy pojazdu do tego przeznaczonego do najbliższego wyjazdu lub MOP),\n" +
                                    "- zatrzymywanie się na pasie rozdzielającym jezdnie,\n" +
                                    "- zatrzymywanie i postój w miejscach niewyznaczonych do tego celu",
                            null,
                            7,
                            sublectures.get(2)
                    ),
                    new Subject(
                            "Droga ekspresowa może mieć jedną lub dwie jezdnie. Również jest oznakowana specjalnymi znakami i przeznaczona wyłącznie do ruchu pojazdów samochodowych (wyłączając czterokołowce). Skrzyżowania na tego typu drodze zdarzają się sporadycznie.",
                            "Znak D-7, informujący o początku drogi ekspresowej.",
                            convertImage("/data/lectureImages/poczatekDrogiEkspresowej.png"),
                            8,
                            sublectures.get(3)
                    ),
                    new Subject(
                            null,
                            "Znak D-8, sygnalizujący koniec drogi ekspresowej.",
                            convertImage("/data/lectureImages/koniecDrogiEkspresowej.png"),
                            9,
                            sublectures.get(3)
                    ),
                    new Subject(
                            "Na drodze ekspresowej zabronione jest:",
                            "- cofanie,\n" +
                                    "- zawracanie (wyłącznie dozwolone na skrzyżowaniach lub w specjalnie wyznaczonych miejscach),\n" +
                                    "- zatrzymywanie się na pasie rozdzielającym jezdnie,\n" +
                                    "- zatrzymywanie i postój w miejscach nieprzeznaczonych do tego celu",
                            null,
                            10,
                            sublectures.get(3)
                    )
            ));


            lectureRepository.saveAll(lectures);
            sublectureRepository.saveAll(sublectures);
            subjectRepository.saveAll(subjects);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        logger.info("Loaded {} lectures, {} sublectures, {} subjects in {} to database.", lectures.size(), sublectures.size(), subjects.size(), formatDuration(System.nanoTime() - startTime));
    }

    private void loadInstructionEvents() {
        long startTime = System.nanoTime();

        SchoolUser instructor = schoolUsers.get(3);
        SchoolUser instructor2 = schoolUsers.get(4);
        SchoolUser instructor3 = schoolUsers.get(5);

        Random random = new Random();
        for (int i = 0; i < Constants.getAllEventTypes().size() / 5; i++) {
            int dayOffset = random.nextInt(29) - 14;
            int startHour = random.nextInt(9) + 8;
            int startMinute = (random.nextInt(4)) * 15;
            LocalDateTime startTimeEvent = LocalDateTime.now().plusDays(dayOffset).withHour(startHour).withMinute(startMinute);
            LocalDateTime endTimeEvent = startTimeEvent.plusHours(2);
            SchoolUser assignedInstructor;
            int randInstructor = random.nextInt(3);
            if (randInstructor == 0) assignedInstructor = instructor;
            else if (randInstructor == 1) assignedInstructor = instructor2;
            else assignedInstructor = instructor3;

            String subject = switch (Constants.getAllEventTypes().get(i)) {
                case Constants.INTRODUCTORY_MEETING -> "Wprowadzenie do kursu - Plan zajęć i cele szkolenia";
                case Constants.BASIC_OVERVIEW ->
                        "Omówienie podstaw - Co trzeba wiedzieć przed rozpoczęciem nauki jazdy";
                case Constants.THEORY_PRESENTATION -> "Prezentacja teoretyczna - Przepisy drogowe i zasady ruchu";
                case Constants.PRACTICAL_INTRODUCTION -> "Praktyczne wprowadzenie - Pierwsze kroki za kierownicą";
                case Constants.SAFETY_WORKSHOP ->
                        "Warsztat bezpieczeństwa - Zagrożenia na drodze i sposoby unikania wypadków";
                case Constants.ROAD_RULES_SEMINAR -> "Seminarium przepisów drogowych - Szczegółowa analiza zasad ruchu";
                case Constants.FIRST_AID_COURSE ->
                        "Kurs pierwszej pomocy - Podstawy ratowania życia w sytuacjach drogowych";
                case Constants.DRIVER_PREPARATION -> "Przygotowanie kierowcy - Najważniejsze umiejętności i nawyki";
                case Constants.ORGANIZATIONAL_SESSION -> "Sesja praktyczna jazdy";
                case Constants.HIGHWAY_DRIVING_TRAINING ->
                        "Szkolenie z jazdy autostradowej - Bezpieczne wyższe prędkości";
                case Constants.NIGHT_DRIVING_OVERVIEW -> "Nocna jazda - Techniki i środki ostrożności";
                case Constants.ECO_DRIVING_DISCUSSION ->
                        "Dyskusja o jeździe ekonomicznej - Oszczędzanie paliwa i ekologia";
                case Constants.EMERGENCY_BRAKING_DRILL ->
                        "Ćwiczenia gwałtownego hamowania - Reakcja na nagłe zdarzenia";
                case Constants.PRACTICAL_EXAM_TIPS ->
                        "Wskazówki do egzaminu praktycznego - Jak zdać za pierwszym razem";
                case Constants.THEORETICAL_EXAM_GUIDE -> "Poradnik egzaminu teoretycznego - Skuteczne strategie nauki";
                case Constants.GROUP_DISCUSSION -> "Dyskusja grupowa - Wymiana doświadczeń kursantów";
                case Constants.TRAFFIC_SIGNS_REVIEW ->
                        "Przegląd znaków drogowych - Najważniejsze znaki i ich znaczenie";
                case Constants.BASIC_MANEUVERS_PRACTICE ->
                        "Praktyka podstawowych manewrów - Skręcanie, zatrzymywanie, zawracanie";
                case Constants.PASSENGER_SAFETY_OVERVIEW -> "Bezpieczeństwo pasażerów - Odpowiedzialność kierowcy";
                case Constants.FINAL_REVISION -> "Ostateczna powtórka - Podsumowanie całego kursu";
                case Constants.EMERGENCY_PROCEDURES_SEMINAR ->
                        "Seminarium procedur awaryjnych - Co robić w razie kolizji";
                case Constants.PRACTICAL_QA_SESSION ->
                        "Sesja pytań i odpowiedzi praktycznych - Rozwiązywanie wątpliwości kursantów";
                case Constants.INTRO_TO_MANUAL_TRANSMISSION ->
                        "Wprowadzenie do skrzyni manualnej - Zasady zmiany biegów i wyczucia sprzęgła";
                case Constants.INTRO_TO_AUTOMATIC_TRANSMISSION ->
                        "Wprowadzenie do skrzyni automatycznej - Komfort i uproszczenie jazdy";
                case Constants.RURAL_DRIVING_GUIDE ->
                        "Poradnik jazdy poza miastem - Techniki i dobre praktyki na drogach lokalnych";
                case Constants.SUBURBAN_TEST_DRIVE ->
                        "Testowe jazdy podmiejskie - Łączenie ruchu miejskiego i szybszych tras";
                case Constants.REFRESHER_LESSONS ->
                        "Lekcje przypominające - Powtórka kluczowych umiejętności po przerwie";
                case Constants.PRACTICAL_MANEUVERING_SESSION ->
                        "Sesja manewrowania w praktyce - Doskonalenie precyzji i parkowania";
                case Constants.STRATEGIC_DRIVING_TACTICS ->
                        "Strategiczne taktyki jazdy - Planowanie trasy i ocena zagrożeń";
                case Constants.CRITICAL_DECISION_TRAINING ->
                        "Szkolenie z krytycznych decyzji - Jak reagować w ekstremalnych sytuacjach";
                case Constants.DEFENSIVE_DRIVING_SEMINAR ->
                        "Seminarium jazdy defensywnej - Przewidywanie zachowań innych kierowców";
                case Constants.EMERGENCY_STOP_TRAINING ->
                        "Szkolenie z hamowania awaryjnego - Najważniejsze odruchy kierowcy";
                case Constants.PRACTICAL_EXAM_PRACTICE ->
                        "Praktyka przed egzaminem - Symulacja prawdziwego egzaminu państwowego";
                case Constants.HAZARD_PERCEPTION_TRAINING ->
                        "Szkolenie z rozpoznawania zagrożeń - Szybka ocena sytuacji na drodze";
                case Constants.EXAM_SIMULATION -> "Symulacja egzaminu końcowego - Próba generalna dla kursantów";
                case Constants.THEORY_PRACTICE_CONNECT ->
                        "Połączenie teorii z praktyką - Jak wykorzystać wiedzę w codziennej jeździe";
                case Constants.ACCIDENT_PREVENTION_TALK ->
                        "Prelekcja o zapobieganiu wypadkom - Statystyki i skuteczne strategie";
                case Constants.SPEED_MANAGEMENT_WORKSHOP ->
                        "Warsztat zarządzania prędkością - Bezpieczna dynamika jazdy";
                case Constants.ADAPTIVE_DRIVING_SEMINAR ->
                        "Seminarium jazdy adaptacyjnej - Dopasowanie techniki do warunków";
                case Constants.LANE_CHANGING_EXERCISES ->
                        "Ćwiczenia zmiany pasów - Pewne manewry na drogach wielopasmowych";
                case Constants.HEADLIGHT_USAGE_WORKSHOP ->
                        "Warsztat użycia świateł - Prawidłowe oświetlenie w różnych warunkach";
                case Constants.MOUNTAIN_ROAD_GUIDANCE ->
                        "Instruktaż jazdy górskiej - Technika jazdy na stromych podjazdach i zjazdach";
                case Constants.WINTER_DRIVING_PREPARATION ->
                        "Przygotowanie do jazdy zimowej - Opony, technika i ostrożność";
                case Constants.VEHICLE_MAINTENANCE_TALK ->
                        "Prelekcja o utrzymaniu pojazdu - Kontrole okresowe i drobne naprawy";
                case Constants.DOCUMENTATION_WORKSHOP ->
                        "Warsztat dokumentacyjny - Formalności potrzebne do egzaminu i rejestracji";
                case Constants.ROAD_SAFETY_DISCUSSION ->
                        "Dyskusja o bezpieczeństwie na drodze - Kultura jazdy i odpowiedzialność społeczna";
                case Constants.ROAD_ETIQUETTE_DISCUSSION ->
                        "Omówienie kultury jazdy - Współpraca kierowców i zasady uprzejmości";
                case Constants.GROUP_REFLECTION_MEETING ->
                        "Spotkanie refleksyjne w grupie - Podsumowanie dotychczasowych postępów";
                case Constants.SUMMARY_MEETING -> "Spotkanie podsumowujące - Analiza wyników i dalsze kroki";
                case Constants.CLOSING_CEREMONY ->
                        "Ceremonia zakończenia - Wręczenie certyfikatów i podsumowanie kursu";
                default -> "Dodatkowe warsztaty - Temat nieokreślony";
            };

            InstructionEvent event = new InstructionEvent(
                    subject,
                    Constants.getAllEventTypes().get(i),
                    startTimeEvent,
                    endTimeEvent,
                    assignedInstructor,
                    2
            );
            instructionEvents.add(event);
        }


        instructionEventRepository.saveAll(instructionEvents);

        logger.info("Loaded {} instructionEvents in {} to database.", instructionEvents.size(), formatDuration(System.nanoTime() - startTime));
    }

    private void loadMails() {
        long startTime = System.nanoTime();

        SchoolUser admin = schoolUsers.get(0);
        SchoolUser student = schoolUsers.get(1);
        SchoolUser student2 = schoolUsers.get(2);
        SchoolUser instructor = schoolUsers.get(3);
        SchoolUser instructor2 = schoolUsers.get(4);
        SchoolUser instructor3 = schoolUsers.get(5);

        mails.addAll(List.of(mailService.sendMail(admin, student, "Witamy w naszej szkole jazdy!", "Drogi Studentcie,\n\nWitamy w naszej szkole jazdy. Cieszymy się, że dołączyłeś do nas!\n\nPozdrawiam,\nZespół Administracyjny", null),
                new Mail("Drogi Administratorze!,\n\nDziękuję za powitanie. Cieszę się i liczę na owocną współpracę.\n\nPozdrawiam,\nStudent"),
                mailService.sendMail(admin, student2, "Powitanie nowego studenta", "Drogi Studentcie,\n\nWitamy w naszej szkole jazdy. Jesteśmy zadowoleni, że dołączyłeś do naszego zespołu.\n\nPozdrawiam,\nZespół Administracyjny", null),
                new Mail("Drogi Adminie,\n\nDziękuję za ciepłe przywitanie. Jestem chętny, aby rozpocząć naukę.\n\nPozdrawiam,\nStudent"),
                mailService.sendMail(instructor, student, "Plan lekcji na ten tydzień", "Drogi Studentcie,\n\nZamieściłem już na ten tydzień w kalendarzu spotkania. Proszę zapoznaj się z nimi.\n\nPozdrawiam,\nInstruktor", null),
                new Mail("Drogi Instruktorze,\n\nDziękuję za przesłanie informacji. Wszystko jest jasne oraz zapisałem się na dogodne mi terminy.\n\nPozdrawiam,\nStudent"),
                mailService.sendMail(admin, instructor, "Funkcjonalność dodawania materiałów szkoleniowych", "Drogi Instruktorze,\n\nUprzejmnie powiadamiam, iż masz możliwość zamieszczania nowych materiałów szkoleniowych w zakładce \"Wykłady.\"\n\nPozdrawiam,\nZespół Administracyjny", null),
                new Mail("Drogi Adminie,\n\nDziękuję za przypomnienie. Zapoznam się z tą możliwością, ale widzę już, że bedzie to naprawdę przydatna funkcjonalność.\n\nPozdrawiam,\nInstruktor"),
                new Mail("Drogi Instruktorze,\n\nCieszę się bardzo. Jeśli masz jakiekolwiek problemy, nie wahaj się pytać.\n\nPozdrawiam,\nZespół Administracyjny"),
                mailService.sendMail(instructor2, student2, "Harmonogram egzaminów praktycznych", "Drogi Studentcie,\n\nZobacz mój harmonogram egzaminów praktycznych. Proszę zapoznaj się z terminami.\n\nPozdrawiam,\nInstruktor", null),
                new Mail("Drogi Instruktorze,\n\nDziękuję za informacje na temat harmonogramu. Będę się przygotowywać zgodnie z planem.\n\nPozdrawiam,\nStudent"),
                mailService.sendMail(instructor3, student, "Informacje o nowym kursie", "Drogi Studentcie,\n\nInformuję Cię o nowo stworzonym kursie dla Ciebie dotyczącym zaawansowanych technik jazdy. Kurs rozpoczynie się niedługo.\n\nPozdrawiam,\nInstruktor", null),
                new Mail("Drogi Instruktorze,\n\nDziękuję za informację. Jestem zainteresowany udziałem w kursie.\n\nPozdrawiam,\nStudent"),
                new Mail("Drogi Studentcie,\n\nCieszę się, że jesteś zainteresowany. Szczegóły dotyczące zapisów na spotkania znajdziesz wkrótce w moim terminarzu.\n\nPozdrawiam,\nInstruktor")));

        mailService.replyOnMail(mails.get(1), mails.get(0).getId(), student);
        mailService.replyOnMail(mails.get(3), mails.get(2).getId(), student2);
        mailService.replyOnMail(mails.get(5), mails.get(4).getId(), student);
        mailService.replyOnMail(mails.get(7), mails.get(6).getId(), instructor);
        mailService.replyOnMail(mails.get(8), mails.get(6).getId(), admin);
        mailService.replyOnMail(mails.get(10), mails.get(9).getId(), student2);
        mailService.replyOnMail(mails.get(12), mails.get(11).getId(), student);
        mailService.replyOnMail(mails.get(13), mails.get(12).getId(), instructor3);

        logger.info("Loaded {} mails sent to Users in {} to database.", mails.size(), formatDuration(System.nanoTime() - startTime));
    }

    private void loadDoneQuestionsInTestsByUser() {
        long startTime = System.nanoTime();
        Random random = new Random();
        List<SchoolUser> allUsers = schoolUserRepository.findAll();
        Map<String, double[]> distributionMap = new HashMap<>();

        for (SchoolUser schoolUser : allUsers) {
            List<Category> userCategories = schoolUser.getAvailableCategories();

            for (Category category : userCategories) {
                String key = schoolUser.getId() + "_" + category.getNameCategory();
                double d1 = random.nextDouble();
                double d2 = random.nextDouble();
                double sum = d1 + d2 + 1.0;

                double oldCorrect = d1 / sum;
                double oldWrong = d2 / sum;
                double oldSkip = 1.0 / sum;

                distributionMap.put(key, new double[]{oldSkip, oldWrong, oldCorrect});
            }
        }

        for (SchoolUser schoolUser : allUsers) {
            List<Test> availableTests = new ArrayList<>();
            schoolUser.getAvailableCategories().forEach(category -> {
                availableTests.addAll(testService.getAllTestsByCategory(category.getNameCategory()));
            });

            for (Test test : availableTests) {
                List<Question> questions = test.getQuestions();
                if (questions.isEmpty()) continue;

                double fraction = random.nextDouble();
                int howManyQuestionsToAnswer = (int) Math.round(fraction * questions.size());

                Collections.shuffle(questions);
                List<Question> answeredQuestions = questions.subList(0, howManyQuestionsToAnswer);

                String testCategory = test.getDrivingCategory();
                if (testCategory == null) continue;

                String key = schoolUser.getId() + "_" + testCategory;
                double[] probs = distributionMap.get(key);
                if (probs == null) continue;
                double pCorrect = probs[0];
                double pWrong = probs[1];

                for (Question question : answeredQuestions) {
                    StudentAnswersTest sat = new StudentAnswersTest();
                    sat.setSchoolUser(schoolUser);
                    sat.setTest(test);
                    sat.setQuestion(question);

                    long randomSeconds = random.nextInt(36) + 5;
                    sat.setDurationOfAnswer(randomSeconds);

                    double val = random.nextDouble();
                    if (val < pCorrect) {
                        sat.setCorrectness(true);
                        sat.setSkipped(false);
                    } else if (val < pCorrect + pWrong) {
                        sat.setCorrectness(false);
                        sat.setSkipped(false);
                    } else {
                        sat.setCorrectness(false);
                        sat.setSkipped(true);
                    }

                    studentAnswersTests.add(sat);
                }
            }
        }

        studentAnswersTestRepository.saveAll(studentAnswersTests);
        studentAnswersTests.stream().toList().forEach(testStatisticsService::updateStatisticsAnswersForUser);

        logger.info("Loaded {} done questions by {} users in {} to database.", studentAnswersTests.size(), allUsers.size(), formatDuration(System.nanoTime() - startTime));
    }

    private void loadDoneExamsByUser() {
        long startTime = System.nanoTime();
        Random random = new Random();
        List<SchoolUser> allUsers = schoolUserRepository.findAll();

        List<StudentExam> examsToSave = new ArrayList<>();
        List<StudentExamAnswer> answersToSave = new ArrayList<>();

        for (SchoolUser schoolUser : allUsers) {
            List<Category> userCategories = schoolUser.getAvailableCategories();

            for (Category category : userCategories) {
                List<Question> questionSet = studentExamService.generateQuestionSet(category.getNameCategory());
                if (questionSet.size() < 32) continue;

                long randomMinutes = 5 + random.nextInt(21);

                double pCorrect = 0.6 + (random.nextDouble() * 0.4);
                double pNotCorrect = 1.0 - pCorrect;

                double fractionSkip = 0.3 + (0.3 * random.nextDouble());
                double pSkip = pNotCorrect * fractionSkip;
                double pWrong = pNotCorrect - pSkip;

                StudentExam studentExam = new StudentExam();
                studentExam.setSchoolUser(schoolUser);
                studentExam.setCategory(category.getNameCategory());
                studentExam.setPoints(0L);
                studentExam.setStartTime(LocalDateTime.now().minusMinutes(randomMinutes));
                examsToSave.add(studentExam);

                for (Question question : questionSet) {
                    StudentExamAnswer studentExamAnswer = new StudentExamAnswer();
                    studentExamAnswer.setStudentExam(studentExam);
                    studentExamAnswer.setQuestion(question);

                    double val = random.nextDouble();
                    if (val < pCorrect) {
                        studentExamAnswer.setCorrectness(true);
                        studentExamAnswer.setAnswer("correct answer");
                        studentExam.setPoints(studentExam.getPoints() + question.getPoints());
                    } else {
                        double val2 = random.nextDouble();
                        double ratioWrong = pWrong / pNotCorrect;
                        studentExamAnswer.setCorrectness(false);
                        if (val2 < ratioWrong) {
                            studentExamAnswer.setAnswer("wrong answer");
                        } else {
                            studentExamAnswer.setAnswer("");
                        }
                    }
                    answersToSave.add(studentExamAnswer);
                }
            }
        }

        studentExamRepository.saveAll(examsToSave);
        studentExamAnswerRepository.saveAll(answersToSave);

        for (StudentExam exam : examsToSave) {
            studentExamService.setSummaryOfExam(studentExamService.getStudentExamById(exam.getId()));
            studentExams.add(exam);
        }

        logger.info("Loaded {} done exams by {} users in {} to database.", studentExams.size(), allUsers.size(), formatDuration(System.nanoTime() - startTime));
    }

    public Test addTestToDb(String name, Boolean isSpecialistic, String category) {
        Test test = new Test();
        test.setName(name);
        test.setDrivingCategory(category);
        test.setTestType(isSpecialistic);

        try {
            test.setImage(convertSvg("/data/testImageSvgs/" + name + ".svg"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        test.setNumberQuestions(0);
        return test;
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

    public static byte[] convertImage(String resourcePath) throws Exception {
        ClassPathResource imgFile = new ClassPathResource(resourcePath);
        BufferedImage bImage = ImageIO.read(imgFile.getInputStream());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos);
        return bos.toByteArray();
    }

    public static String formatDuration(long durationNano) {
        long durationMillis = durationNano / 1_000_000;
        if (durationMillis < 1000) {
            return durationMillis + "ms";
        }

        long durationSeconds = durationMillis / 1000;
        if (durationSeconds < 60) {
            return durationSeconds + "s";
        }

        long minutes = durationSeconds / 60;
        long seconds = durationSeconds % 60;
        return minutes + "m " + seconds + "s";
    }
}