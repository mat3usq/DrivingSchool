package com.driving.school.utils;

import com.driving.school.model.*;
import com.driving.school.repository.*;
import com.driving.school.service.MailService;
import com.driving.school.service.QuestionService;
import com.driving.school.service.TestService;
import com.driving.school.service.UserStatisticService;
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
public class DatabaseSeeder implements CommandLineRunner {
    private final QuestionService questionService;
    private final SchoolUserRepository schoolUserRepository;
    private final LectureRepository lectureRepository;
    private final SublectureRepository sublectureRepository;
    private final SubjectRepository subjectRepository;
    private final TestService testService;
    private final InstructionEventRepository eventRepository;
    private final MentorShipRepository mentorShipRepository;
    private final CategoryRepository categoryRepository;
    private final MailService mailService;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final PaymentRepository paymentRepository;
    private final CourseRepository courseRepository;
    private final UserStatisticService userStatisticService;
    private final UserStatisticRepository userStatisticRepository;


    @Autowired
    public DatabaseSeeder(QuestionService questionService, SchoolUserRepository
            schoolUserRepository, LectureRepository lectureRepository, SublectureRepository sublectureRepository,
                          SubjectRepository subjectRepository, TestService testService, InstructionEventRepository eventRepository,
                          MentorShipRepository mentorShipRepository, CategoryRepository categoryRepository,
                          MailService mailService, QuestionRepository questionRepository, TestRepository testRepository, PaymentRepository paymentRepository,
                          CourseRepository courseRepository,UserStatisticService userStatisticService,
                          UserStatisticRepository userStatisticRepository) {
        this.questionService = questionService;
        this.schoolUserRepository = schoolUserRepository;
        this.lectureRepository = lectureRepository;
        this.sublectureRepository = sublectureRepository;
        this.subjectRepository = subjectRepository;
        this.testService = testService;
        this.eventRepository = eventRepository;
        this.mentorShipRepository = mentorShipRepository;
        this.categoryRepository = categoryRepository;
        this.mailService = mailService;
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
        this.paymentRepository = paymentRepository;
        this.courseRepository = courseRepository;
        this.userStatisticRepository = userStatisticRepository;
        this.userStatisticService = userStatisticService;
    }


    @Override
    public void run(String... args) {
        if (schoolUserRepository.findByEmail("admin") != null) {
            logger.info("Dane zostały zasadzone, pomijam.");
            return;
        }
        createUsersAndCategoriesAndTests();
        createUserStatistics();
        createLectures();
        mapQuestionsToDb();
        createEvents();
        createMails();
    }


    private void createUserStatistics() { // Dodane
        logger.info("Tworzenie przykładowych UserStatistic dla użytkownika 'student2'...");

        // Pobierz użytkownika 'student2'
        SchoolUser student2 = schoolUserRepository.findByEmail("student2");


        // Pobierz kategorie A, B, D
        List<Category> categories = categoryRepository.findAll().stream()
                .filter(c -> Arrays.asList("A", "B").contains(c.getNameCategory()))
                .collect(Collectors.toList());


        // Tworzenie przykładowych UserStatistic
        List<UserStatistic> userStatistics = new ArrayList<>();

        for (Category category : categories) {
            UserStatistic us = new UserStatistic();
            us.setDeposits(100L); // Przykładowa wartość
            us.setIsExamPassed("No"); // Przykładowa wartość
            us.setHoursed("10"); // Przykładowa wartość
            us.setHoursBuyed("5"); // Przykładowa wartość
            us.setSchoolUser(student2);
            us.setCategory(category);
            userStatistics.add(us);
        }

        userStatisticRepository.saveAll(userStatistics);
        logger.info("Przykładowe UserStatistic zostały utworzone dla użytkownika 'student2'.");
    }


    private void createUsersAndCategoriesAndTests() {
        // haslo: admin
        // "A,B,C,D,T,AM,A1,A2,B1,C1,D1,PT"
        SchoolUser admin = new SchoolUser("admin", "admin", "$100801$cfJJlxSl83FjJ2mh+6yUcdxxksVm3XlOzhBr4gLHFEOhdeWmaf2H6Lki/fe99YUMduDoX/LGHUcodWe9SkhVnw==$Q1yyulzoYXseBJ/OmM/xgcYD9fFPaw2bRzBHRW7RgG4=", "admin", Constants.ADMIN_ROLE, "B");

        // haslo: student
        // "A,B,D"
        SchoolUser student = new SchoolUser("student", "student", "$100801$CzaxAyZkwycp18sGzcZE33ymaEBuqHY579JJ8CzRdckDIUMYQADzXGPRE2Hqz3iZauxyIkkSbo3998KrBYVznA==$vbVS5qCtrKps3saxR7pmK+pA+TNiZQfNWwrcHS7qHuo=", "student", Constants.STUDENT_ROLE, "");
        // ""
        SchoolUser student2 = new SchoolUser("student2", "student2", "$100801$CzaxAyZkwycp18sGzcZE33ymaEBuqHY579JJ8CzRdckDIUMYQADzXGPRE2Hqz3iZauxyIkkSbo3998KrBYVznA==$vbVS5qCtrKps3saxR7pmK+pA+TNiZQfNWwrcHS7qHuo=", "student2", Constants.STUDENT_ROLE, "");

        // haslo: instructor
        // "A,B,C,D,T,AM,A1,A2,B1,C1,D1,PT"
        SchoolUser instructor = new SchoolUser("instructor", "instructor", "$100801$mtcGeB1wJkCJufG6sWa/FJ110+v5R9nIhvFhccGm6IuTc9mA43NJQNQVz8Gbjy5XepW7tWaaI8QM7bpVDd0rmA==$gxqwRbGBy2s5ztOWgJRwfh2+TZJZvgfZCZSXHlQYE5k=", "instructor", Constants.INSTRUCTOR_ROLE, "");
        SchoolUser instructor2 = new SchoolUser("instructor2", "instructor2", "$100801$mtcGeB1wJkCJufG6sWa/FJ110+v5R9nIhvFhccGm6IuTc9mA43NJQNQVz8Gbjy5XepW7tWaaI8QM7bpVDd0rmA==$gxqwRbGBy2s5ztOWgJRwfh2+TZJZvgfZCZSXHlQYE5k=", "instructor2", Constants.INSTRUCTOR_ROLE, "");
        SchoolUser instructor3 = new SchoolUser("instructor3", "instructor3", "$100801$mtcGeB1wJkCJufG6sWa/FJ110+v5R9nIhvFhccGm6IuTc9mA43NJQNQVz8Gbjy5XepW7tWaaI8QM7bpVDd0rmA==$gxqwRbGBy2s5ztOWgJRwfh2+TZJZvgfZCZSXHlQYE5k=", "instructor3", Constants.INSTRUCTOR_ROLE, "");

        schoolUserRepository.save(admin);
        schoolUserRepository.save(student);
        schoolUserRepository.save(student2);
        schoolUserRepository.save(instructor);
        schoolUserRepository.save(instructor2);
        schoolUserRepository.save(instructor3);

        // "A,B,C,D,T,AM,A1,A2,B1,C1,D1,PT"
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("A", Arrays.asList(admin, instructor, instructor2, instructor3), "20 lat - jeśli masz już od co najmniej 2 lat prawo jazdy kategorii A2 /\\ 24 lata - jeśli nie masz od co najmniej 2 lat prawa jazdy kategorii A2",
                Arrays.asList("każdym motocyklem,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM,",
                        "zespołem pojazdów złożonym z pojazdu określonego dla tej kategorii wraz z przyczepą – tylko w Polsce.")));
        categories.add(new Category("B", Arrays.asList(admin, instructor, instructor2, instructor3), "18 lat",
                Arrays.asList("pojazdem samochodowym o dopuszczalnej masie całkowitej nieprzekraczającej 3,5 t, z wyjątkiem autobusu i motocykla,",
                        "zespołem pojazdów złożonym z pojazdu, o którym mowa wyżej oraz z przyczepy lekkiej,",
                        "zespołem pojazdów samochodowych o dopuszczalnej masie całkowitej nieprzekraczającej 3,5 t oraz z przyczepy innej niż lekka, o ile łączna dopuszczalna masa całkowita zespołu tych pojazdów nie przekracza 4250 kg,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM,",
                        "ciągnikiem rolniczym, pojazdem wolnobieżnym oraz zespołem złożonym z tego pojazdu i przyczepy lekkiej – tylko w Polsce,",
                        "motocyklem o pojemności skokowej silnika nieprzekraczającej 125 cm3, mocy nieprzekraczającej 11 kW i stosunku mocy do masy własnej nieprzekraczającym 0,1 kW/kg, lub motocyklem trójkołowym, pod warunkiem że osoba posiada prawo jazdy kategorii B od co najmniej 3 lat,",
                        "pojazdem samochodowym zasilanym paliwami alternatywnymi o dopuszczalnej masie całkowitej przekraczającej 3,5 t oraz nieprzekraczającej 4250 kg, jeżeli przekroczenie dopuszczalnej masy całkowitej 3,5 t wynika z zastosowania paliw alternatywnych, o których mowa w przepisach wydanych na podstawie art. 66 ust. 5 ustawy z dnia 20 czerwca 1997 r. - Prawo o ruchu drogowym, a informacja o tym przekroczeniu jest odnotowana w dowodzie rejestracyjnym, pod warunkiem że osoba posiada prawo jazdy kategorii B od co najmniej 2 lat.")));
        categories.add(new Category("C", Arrays.asList(admin, instructor, instructor2, instructor3), "21 lat",
                Arrays.asList("pojazdem samochodowym o dopuszczalnej masie całkowitej przekraczającej 3,5 t, z wyjątkiem autobusu,",
                        "zespołem pojazdów złożonym z pojazdu, o którym mowa wyżej oraz z przyczepy lekkiej,",
                        "ciągnikiem rolniczym, pojazdem wolnobieżnym oraz zespołem złożonym z tego pojazdu i przyczepy lekkiej – tylko w Polsce,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM.")));
        categories.add(new Category("D", Arrays.asList(admin, instructor, instructor2, instructor3), "24 lata",
                Arrays.asList("autobusem,",
                        "zespołem pojazdów złożonym z pojazdu, o którym wyżej oraz z przyczepy lekkiej,",
                        "ciągnikiem rolniczym, pojazdem wolnobieżnym oraz zespołem złożonym z tego pojazdu i przyczepy lekkiej – tylko w Polsce,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM.")));
        categories.add(new Category("T", Arrays.asList(admin, instructor, instructor2, instructor3), "16 lat",
                Arrays.asList("ciągnikiem rolniczym lub pojazdem wolnobieżnym,",
                        "zespołem pojazdów złożonym z ciągnika rolniczego z przyczepą (przyczepami) lub pojazdem wolnobieżnym z przyczepą (przyczepami),",
                        "pojazdami określonymi dla prawa jazdy kategorii AM.")));
        categories.add(new Category("AM", Arrays.asList(admin, instructor, instructor2, instructor3), "14 lat",
                Arrays.asList("motorowerem,", "czterokołowcem lekkim (tzw. małym quadem),", "zespołem pojazdów złożonym z pojazdu określonego dla tej kategorii wraz z przyczepą – tylko w Polsce.")));
        categories.add(new Category("A1", Arrays.asList(admin, instructor, instructor2, instructor3), "16 lat",
                Arrays.asList("motocyklem o pojemności skokowej silnika nieprzekraczającej 125 cm3, mocy nieprzekraczającej 11 kW i stosunku mocy do masy własnej nieprzekraczającym 0,1 kW/kg,",
                        "motocyklem trójkołowym o mocy nieprzekraczającej 15 kW,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM,",
                        "zespołem pojazdów złożonym z pojazdu określonego dla tej kategorii wraz z przyczepą – tylko w Polsce.")));
        categories.add(new Category("A2", Arrays.asList(admin, instructor, instructor2, instructor3), "18 lat",
                Arrays.asList("motocyklem o mocy nieprzekraczającej 35 kW i stosunku mocy do masy własnej nieprzekraczającym 0,2 kW/kg, przy czym nie może on powstać w wyniku wprowadzenia zmian w pojeździe o mocy przekraczającej dwukrotność mocy tego motocykla,",
                        "motocyklem trójkołowym o mocy nieprzekraczającej 15 kW,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM,",
                        "zespołem pojazdów złożonym z pojazdu określonego dla tej kategorii wraz z przyczepą – tylko w Polsce.")));
        categories.add(new Category("B1", Arrays.asList(admin, instructor, instructor2, instructor3), "16 lat",
                Arrays.asList("czterokołowcem,", "pojazdami określonymi dla prawa jazdy kategorii AM.")));
        categories.add(new Category("C1", Arrays.asList(admin, instructor, instructor2, instructor3), "18 lat",
                Arrays.asList("pojazdem samochodowym o dopuszczalnej masie całkowitej przekraczającej 3,5 t i nieprzekraczającej 7,5 t, z wyjątkiem autobusu,",
                        "zespołem pojazdów złożonym z pojazdu, o którym mowa wyżej oraz z przyczepy lekkiej,",
                        "ciągnikiem rolniczym, pojazdem wolnobieżnym oraz zespołem złożonym z tego pojazdu i przyczepy lekkiej – tylko w Polsce,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM.")));
        categories.add(new Category("D1", Arrays.asList(admin, instructor, instructor2, instructor3), "21 lat",
                Arrays.asList("autobusem przeznaczonym konstrukcyjnie do przewozu nie więcej niż 17 osób łącznie z kierowcą, o długości nieprzekraczającej 8 m,",
                        "zespołem pojazdów złożonym z pojazdu, o którym mowa wyżej oraz z przyczepy lekkiej,",
                        "ciągnikiem rolniczym, pojazdem wolnobieżnym oraz zespołem złożonym z tego pojazdu i przyczepy lekkiej – tylko w Polsce,",
                        "pojazdami określonymi dla prawa jazdy kategorii AM.")));
        categories.add(new Category("PT", Arrays.asList(admin, instructor, instructor2, instructor3), "21 lat",
                Arrays.asList("tramwaj,",
                        "ciągnik rolniczy i pojazd wolnobieżny,",
                        "zespół pojazdów złożony z ciągnika rolniczego lub pojazdu wolnobieżnego oraz jednej i więcej przyczep,",
                        "pojazd kategorii AM.")));

        categoryRepository.saveAll(categories);

        admin.setAvailableCategories(categories);
        student.setAvailableCategories(Arrays.asList(categories.get(0), categories.get(1), categories.get(3)));
        instructor.setAvailableCategories(categories);
        instructor2.setAvailableCategories(categories);
        instructor3.setAvailableCategories(categories);

        schoolUserRepository.save(admin);
        schoolUserRepository.save(student);
        schoolUserRepository.save(instructor);
        schoolUserRepository.save(instructor2);
        schoolUserRepository.save(instructor3);

        MentorShip ms = new MentorShip(student, instructor, Constants.ACTIVE);
        mentorShipRepository.save(ms);
        mentorShipRepository.save(new MentorShip(student, instructor2, Constants.ACTIVE));
        mentorShipRepository.save(new MentorShip(student2, instructor, Constants.ACTIVE));
        mentorShipRepository.save(new MentorShip(student2, instructor2, Constants.PENDING));

        categories.forEach(this::addTeststoDb);

        Payment paymentFirst = new Payment();
        paymentFirst.setSum(123.32);
        paymentFirst.setCategories(Arrays.asList(categories.get(0), categories.get(1)));
        paymentFirst.setComment("Oplata za pierwsze dwie kategorie :D");
        paymentFirst.setSchoolUser(student);

        Payment paymentSecond = new Payment();
        paymentSecond.setSum(50.13);
        paymentSecond.setCategories(Arrays.asList(categories.get(3)));
        paymentSecond.setComment("Oplata za kategorie D.");
        paymentSecond.setSchoolUser(student);

        paymentRepository.save(paymentFirst);
        paymentRepository.save(paymentSecond);

        Course course = new Course();
        course.setCategory(categories.getFirst());
        course.setMentorShip(ms);
        course.setDescription("Kurs na prawo jazdy doskonalajacy umiejetnosci techniczne oraz doswiadczenie za kierownica.");
        course.setDuration(120.0);
        course.setSummaryDurationHours(6.5);
        course.setSummaryAverageResultTest(66.33);
        course.setDrivingSessions(List.of(new DrivingSession(LocalDateTime.now(), 2.0, "Pierwsza jazda - cwiczenia na parkingu.", course),
                new DrivingSession(LocalDateTime.now(), 1.5, "Druga jazda - zaznajomienie z rondem.", course),
                new DrivingSession(LocalDateTime.now(), 3.0, "Trzecia jazda mozna jechac na egzamin", course)));
        course.setTestCourses(List.of(new TestCourse(LocalDateTime.now(), "Ogolnie ok: do poprawy znaki ostrzegawcze", Constants.COURSE_TEST_THEORETICAL, 96.5, course),
                new TestCourse(LocalDateTime.now(), "Do poprawy: podstawowe pojecia", Constants.COURSE_TEST_GENERAL, 86.5, course),
                new TestCourse(LocalDateTime.now(), "Test zakonczyl sie na placu", Constants.COURSE_TEST_PRACTICAL, 16.0, course)));
        course.setCommentCourses(List.of(new CommentCourse(LocalDateTime.now(), "Przygotwanie do jazdy to podstawa.", course),
                new CommentCourse(LocalDateTime.now(), "Wieksze skupienie podczas jazdy.", course),
                new CommentCourse(LocalDateTime.now(), "Nalezy uwazac na pieszych.", course)));
        courseRepository.save(course);
    }

    private void mapQuestionsToDb() {
        List<Test> tests = testService.getAllTests();
        System.out.print("alalal");

        Map<String, List<Test>> testMapByNameAndType = tests.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getName().toLowerCase() + "_" + t.getTestType()
                ));

        List<Test> otherTests = testRepository.findByName("Inne");
        Map<Boolean, List<Test>> otherTestsByType = otherTests.stream()
                .collect(Collectors.groupingBy(Test::getTestType));

        try (CSVReader reader = new CSVReaderBuilder(new FileReader("src/main/resources/data/questions/questions.csv"))
                .withCSVParser(new CSVParserBuilder()
                        .withSeparator(';')
                        .build())
                .build()) {
            List<String[]> records = reader.readAll();
            int iteration = 1;
            long startTime = System.nanoTime();

            List<Question> questionsToSave = new ArrayList<>();
            Set<Test> testsToSave = new HashSet<>();

            for (String[] record : records) {
                if (iteration == 1) {
                    iteration++;
                    continue;
                }
                Question question = new Question();

                question.setQuestion(record[0]);
                question.setMediaName(record[1]);
                question.setDrivingCategory(record[2]);
                question.setSubjectArea(record[3]);
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
                    questionsToSave.add(question);
                }

                ++iteration;
            }

            questionRepository.saveAll(questionsToSave);
            testRepository.saveAll(new ArrayList<>(testsToSave));

            long durationNano = System.nanoTime() - startTime;
            long durationMillis = durationNano / 1_000_000;
            long durationSeconds = durationMillis / 1000;
            long minutes = durationSeconds / 60;
            long seconds = durationSeconds % 60;
            logger.info("Zmapowano {} pytań w czasie: {} minut {} sekund", iteration, minutes, seconds);

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    private void createLectures() {
        try {
            Lecture l = new Lecture("RUCH POJAZDOW Kategoria B", null, 1, "B");
            Lecture l2 = new Lecture("RUCH POJAZDOW Kategoria D", null, 1, "D");
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


    public void addTeststoDb(Category category) {
        for (String name : TestNames.getNames())
            addTestToDb(name, false, category.getNameCategory());

        for (String name : TestNames.getSpecialTestNames())
            addTestToDb(name, true, category.getNameCategory());
    }

    public void addTestToDb(String name, Boolean isSpecialistic, String category) {
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
        testService.saveTest(test);
    }

    private void createEvents() {
        SchoolUser schoolUser = schoolUserRepository.findByEmail("instructor");
        SchoolUser schoolUser2 = schoolUserRepository.findByEmail("instructor2");
        SchoolUser schoolUser3 = schoolUserRepository.findByEmail("instructor3");
        InstructionEvent ie = new InstructionEvent("Podstawowe zasady jazdy i wprowadzenie do ruchu drogowego", Constants.PRACTICAL_DRIVING_ON_ROADS, LocalDateTime.now().plusHours(1).plusMinutes(1), LocalDateTime.now().plusHours(2).plusMinutes(2), schoolUser, 12);

        List<InstructionEvent> events = Arrays.asList(
                ie,
                new InstructionEvent("Teoria jazdy: Zasady, przepisy i najlepsze praktyki", Constants.THEORY_OF_DRIVING_CLASSES, LocalDateTime.of(2024, 10, 2, 14, 0), LocalDateTime.of(2024, 10, 2, 16, 0), schoolUser, 10),
                new InstructionEvent("Ćwiczenia praktyczne: Bezpieczne poruszanie się po drogach", Constants.PRACTICAL_DRIVING_ON_ROADS, LocalDateTime.of(2024, 10, 3, 9, 0), LocalDateTime.of(2024, 10, 3, 11, 0), schoolUser, 3),
                new InstructionEvent("Spotkanie informacyjne: Wprowadzenie do kursu jazdy", Constants.INFORMATION_SESSIONS, LocalDateTime.of(2024, 10, 4, 15, 0), LocalDateTime.of(2024, 10, 4, 17, 0), schoolUser, 100),
                new InstructionEvent("Trening manewrów: Parkowanie, cofanie i zawracanie", Constants.BASIC_MANEUVERS, LocalDateTime.of(2024, 10, 5, 8, 0), LocalDateTime.of(2024, 10, 5, 10, 0), schoolUser, 54),
                new InstructionEvent("Kurs pierwszej pomocy : Reagowanie na wypadki drogowe", Constants.FIRST_AID_CLASSES, LocalDateTime.of(2024, 10, 6, 13, 0), LocalDateTime.of(2024, 10, 6, 15, 0), schoolUser, 65),
                new InstructionEvent("Jazda miejska: Radzenie sobie z ruchem ulicznym", Constants.CITY_DRIVING, LocalDateTime.of(2024, 10, 7, 11, 0), LocalDateTime.of(2024, 10, 7, 13, 0), schoolUser, 12),
                new InstructionEvent("Zaawansowane techniki jazdy: Skuteczność i bezpieczeństwo", Constants.PRACTICAL_DRIVING_ON_ROADS, LocalDateTime.of(2024, 10, 8, 12, 0), LocalDateTime.of(2024, 10, 8, 14, 0), schoolUser, 54),
                new InstructionEvent("Dogłębna analiza przepisów ruchu drogowego", Constants.THEORY_OF_DRIVING_CLASSES, LocalDateTime.of(2024, 10, 9, 16, 0), LocalDateTime.of(2024, 10, 9, 18, 0), schoolUser, 34),
                new InstructionEvent("Praktyczna nauka jazdy: Techniki zaawansowane", Constants.PRACTICAL_DRIVING_ON_ROADS, LocalDateTime.of(2024, 10, 10, 14, 0), LocalDateTime.of(2024, 10, 10, 16, 0), schoolUser, 34),
                new InstructionEvent("Aktualizacja przepisów drogowych: Co nowego w 2024?", Constants.LECTURES, LocalDateTime.of(2024, 10, 11, 10, 0), LocalDateTime.of(2024, 10, 11, 12, 0), schoolUser, 34),
                new InstructionEvent("Nocna jazda: Jak bezpiecznie poruszać się po zmroku", Constants.NIGHT_DRIVING, LocalDateTime.of(2024, 10, 12, 14, 0), LocalDateTime.of(2024, 10, 12, 16, 0), schoolUser, 34),
                new InstructionEvent("Jazda ekonomiczna: Techniki oszczędzania paliwa", Constants.ECO_DRIVING_CLASSES, LocalDateTime.of(2024, 10, 13, 9, 0), LocalDateTime.of(2024, 10, 13, 11, 0), schoolUser, 54),
                new InstructionEvent("Dodatkowe lekcje: Przygotowanie do egzaminu praktycznego", Constants.ADDITIONAL_DRIVING_LESSONS, LocalDateTime.of(2024, 10, 14, 15, 0), LocalDateTime.of(2024, 10, 14, 17, 0), schoolUser, 12),
                new InstructionEvent("Intensywny trening na placu manewrowym: Doskonalenie umiejętności", Constants.PRACTICAL_CLASSES_ON_MANEUVERING_GROUND, LocalDateTime.of(2024, 10, 15, 8, 0), LocalDateTime.of(2024, 10, 15, 10, 0), schoolUser, 56),
                new InstructionEvent("Warsztaty z technik awaryjnych: Jak reagować w sytuacjach kryzysowych", Constants.EMERGENCY_TECHNIQUES_TRAINING, LocalDateTime.of(2024, 10, 16, 13, 0), LocalDateTime.of(2024, 10, 16, 15, 0), schoolUser, 43),
                new InstructionEvent("Jazda poza miastem: Techniki jazdy na trasach szybkiego ruchu", Constants.OUT_OF_TOWN_DRIVING, LocalDateTime.of(2024, 10, 17, 11, 0), LocalDateTime.of(2024, 10, 17, 13, 0), schoolUser, 54),
                new InstructionEvent("Podstawy obsługi technicznej pojazdu: Co każdy kierowca powinien wiedzieć", Constants.CAR_MAINTENANCE_TRAINING, LocalDateTime.of(2024, 10, 18, 12, 0), LocalDateTime.of(2024, 10, 18, 14, 0), schoolUser, 23),
                new InstructionEvent("Spotkanie organizacyjne: Planowanie harmonogramu kursu", Constants.INFORMATION_AND_ORGANIZATIONAL_MEETINGS, LocalDateTime.of(2024, 10, 19, 16, 0), LocalDateTime.of(2024, 10, 19, 18, 0), schoolUser, 67),
                new InstructionEvent("Konsultacje z egzaminatorem: Przygotowanie do testów", Constants.MEETINGS_WITH_EXAMINERS, LocalDateTime.of(2024, 10, 20, 14, 0), LocalDateTime.of(2024, 10, 20, 16, 0), schoolUser, 67),
                new InstructionEvent("Konsultacje z egzaminatorem: Przygotowanie do testów", Constants.MEETINGS_WITH_EXAMINERS, LocalDateTime.of(2024, 11, 20, 14, 0), LocalDateTime.of(2024, 11, 20, 16, 0), schoolUser2, 54),
                new InstructionEvent("Driftowanie bokiem", Constants.MEETINGS_WITH_EXAMINERS, LocalDateTime.of(2024, 10, 20, 14, 0), LocalDateTime.of(2024, 10, 20, 16, 0), schoolUser3, 87)
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

    private void createMails() {
        logger.info("Tworzenie przykładowych wiadomości e-mail...");

        SchoolUser admin = schoolUserRepository.findByEmail("admin");
        SchoolUser student = schoolUserRepository.findByEmail("student");
        SchoolUser student2 = schoolUserRepository.findByEmail("student2");
        SchoolUser instructor = schoolUserRepository.findByEmail("instructor");
        SchoolUser instructor2 = schoolUserRepository.findByEmail("instructor2");
        SchoolUser instructor3 = schoolUserRepository.findByEmail("instructor3");

        if (admin == null || student == null || student2 == null || instructor == null
                || instructor2 == null || instructor3 == null) {
            logger.error("Brak wymaganych użytkowników w bazie danych. Upewnij się, że użytkownicy zostali poprawnie utworzeni.");
            return;
        }

        // Przykład 1: Admin wysyła wiadomość do Studenta, Student odpowiada Adminowi
        Mail adminToStudent = mailService.sendMail(
                admin,
                student,
                "Witamy w naszej szkole jazdy!",
                "Drogi Studentcie,\n\nWitamy w naszej szkole jazdy. Cieszymy się, że dołączyłeś do naszego programu.\n\nPozdrawiam,\nZespół Administracyjny",
                null
        );

        if (adminToStudent != null) {
            // Student odpowiada Adminowi
            Mail studentToAdmin = new Mail();
            studentToAdmin.setBody("Drogi Adminie,\n\nDziękuję za serdeczne powitanie. Cieszę się na nadchodzące lekcje.\n\nPozdrawiam,\nStudent");
            boolean replySuccess = mailService.replyOnMail(studentToAdmin, adminToStudent.getId(), student);
            if (!replySuccess) {
                logger.error("Nie udało się wysłać odpowiedzi od Studenta do Admina.");
            }
        }

        // Przykład 2: Admin wysyła wiadomość do Student2, Student2 odpowiada Adminowi
        Mail adminToStudent2 = mailService.sendMail(
                admin,
                student2,
                "Powitanie dla nowego studenta",
                "Drogi Student2,\n\nWitamy w naszej szkole jazdy. Jesteśmy podekscytowani, że dołączyłeś do naszego zespołu.\n\nPozdrawiam,\nZespół Administracyjny",
                null
        );

        if (adminToStudent2 != null) {
            // Student2 odpowiada Adminowi
            Mail student2ToAdmin = new Mail();
            student2ToAdmin.setBody("Drogi Adminie,\n\nDziękuję za powitanie. Jestem gotowy rozpocząć naukę.\n\nPozdrawiam,\nStudent2");
            boolean replySuccess = mailService.replyOnMail(student2ToAdmin, adminToStudent2.getId(), student2);
            if (!replySuccess) {
                logger.error("Nie udało się wysłać odpowiedzi od Student2 do Admina.");
            }
        }

        // Przykład 3: Instruktor wysyła wiadomość do Studenta, Student odpowiada Instruktorowi
        Mail instructorToStudent = mailService.sendMail(
                instructor,
                student,
                "Plan lekcji na ten tydzień",
                "Drogi Student,\n\nPrzesyłam plan lekcji na ten tydzień. Proszę zapoznaj się z harmonogramem.\n\nPozdrawiam,\nInstruktor",
                null
        );

        if (instructorToStudent != null) {
            // Student odpowiada Instruktorowi
            Mail studentToInstructor = new Mail();
            studentToInstructor.setBody("Drogi Instruktorze,\n\nDziękuję za przesłanie planu. Wszystko jest dla mnie jasne.\n\nPozdrawiam,\nStudent");
            boolean replySuccess = mailService.replyOnMail(studentToInstructor, instructorToStudent.getId(), student);
            if (!replySuccess) {
                logger.error("Nie udało się wysłać odpowiedzi od Studenta do Instruktora.");
            }
        }

        // Przykład 4: Admin wysyła wiadomość do Instruktora, Instruktor odpowiada Adminowi, Admin odpowiada Instruktorowi ponownie
        Mail adminToInstructor = mailService.sendMail(
                admin,
                instructor,
                "Nowe materiały szkoleniowe",
                "Drogi Instruktorze,\n\nPrzesyłam nowe materiały szkoleniowe do wykorzystania podczas lekcji.\n\nPozdrawiam,\nZespół Administracyjny",
                null
        );

        if (adminToInstructor != null) {
            // Instruktor odpowiada Adminowi
            Mail instructorToAdmin = new Mail();
            instructorToAdmin.setBody("Drogi Adminie,\n\nDziękuję za przesłanie materiałów. Będę je wykorzystać podczas nadchodzących lekcji.\n\nPozdrawiam,\nInstruktor");
            boolean replySuccess1 = mailService.replyOnMail(instructorToAdmin, adminToInstructor.getId(), instructor);
            if (!replySuccess1) {
                logger.error("Nie udało się wysłać odpowiedzi od Instruktora do Admina.");
            }

            // Admin odpowiada Instruktorowi ponownie
            Mail adminToInstructorReply = new Mail();
            adminToInstructorReply.setBody("Drogi Instruktorze,\n\nCieszę się, że materiały są przydatne. Jeśli masz jakiekolwiek pytania, nie wahaj się pytać.\n\nPozdrawiam,\nZespół Administracyjny");
            boolean replySuccess2 = mailService.replyOnMail(adminToInstructorReply, adminToInstructor.getId(), admin);
            if (!replySuccess2) {
                logger.error("Nie udało się wysłać drugiej odpowiedzi od Admina do Instruktora.");
            }
        }

        // Przykład 5: Instruktor2 wysyła wiadomość do Student2, Student2 odpowiada Instruktor2
        Mail instructor2ToStudent2 = mailService.sendMail(
                instructor2,
                student2,
                "Harmonogram egzaminów praktycznych",
                "Drogi Student2,\n\nPrzesyłam harmonogram egzaminów praktycznych. Proszę zapoznaj się z terminami.\n\nPozdrawiam,\nInstruktor2",
                null
        );

        if (instructor2ToStudent2 != null) {
            // Student2 odpowiada Instruktor2
            Mail student2ToInstructor2 = new Mail();
            student2ToInstructor2.setBody("Drogi Instruktorze2,\n\nDziękuję za przesłanie harmonogramu. Będę się przygotowywać zgodnie z planem.\n\nPozdrawiam,\nStudent2");
            boolean replySuccess = mailService.replyOnMail(student2ToInstructor2, instructor2ToStudent2.getId(), student2);
            if (!replySuccess) {
                logger.error("Nie udało się wysłać odpowiedzi od Student2 do Instruktora2.");
            }
        }

        // Przykład 6: Instruktor3 wysyła wiadomość do Studenta, Student odpowiada Instruktorowi, Instruktor odpowiada Studentowi
        Mail instructor3ToStudent = mailService.sendMail(
                instructor3,
                student,
                "Informacje o nowym kursie",
                "Drogi Student,\n\nInformuję Cię o nowym kursie dotyczącym zaawansowanych technik jazdy. Kurs rozpoczyna się w przyszłym miesiącu.\n\nPozdrawiam,\nInstruktor3",
                null
        );

        if (instructor3ToStudent != null) {
            // Student odpowiada Instruktor3
            Mail studentToInstructor3 = new Mail();
            studentToInstructor3.setBody("Drogi Instruktorze3,\n\nDziękuję za informację. Jestem zainteresowany udziałem w kursie.\n\nPozdrawiam,\nStudent");
            boolean replySuccess1 = mailService.replyOnMail(studentToInstructor3, instructor3ToStudent.getId(), student);
            if (!replySuccess1) {
                logger.error("Nie udało się wysłać odpowiedzi od Studenta do Instruktora3.");
            }

            // Instruktor3 odpowiada Studentowi ponownie
            Mail instructor3ToStudentReply = new Mail();
            instructor3ToStudentReply.setBody("Drogi Student,\n\nCieszę się, że jesteś zainteresowany. Szczegóły dotyczące zapisów i harmonogramu prześlę wkrótce.\n\nPozdrawiam,\nInstruktor3");
            boolean replySuccess2 = mailService.replyOnMail(instructor3ToStudentReply, instructor3ToStudent.getId(), instructor3);
            if (!replySuccess2) {
                logger.error("Nie udało się wysłać drugiej odpowiedzi od Instruktora3 do Studenta.");
            }
        }

        logger.info("Przykładowe wiadomości e-mail zostały utworzone.");
    }

}