package com.driving.school.utils;

import com.driving.school.model.*;
import com.driving.school.repository.*;
import com.driving.school.service.LectureService;
import com.driving.school.service.QuestionService;
import com.driving.school.service.SchoolUserService;
import com.driving.school.service.TestService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
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

    @Autowired
    public DatabaseSeeder(QuestionService questionService, SchoolUserRepository
            schoolUserRepository, LectureRepository lectureRepository, SublectureRepository sublectureRepository,
                          SubjectRepository subjectRepository, TestService testService, InstructionEventRepository eventRepository) {
        this.questionService = questionService;
        this.schoolUserRepository = schoolUserRepository;
        this.lectureRepository = lectureRepository;
        this.sublectureRepository = sublectureRepository;
        this.subjectRepository = subjectRepository;
        this.testService = testService;
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(String... args) {
        mapQuestionsToDb();
        createUsers();
        createLectures();
        addTeststoDb();
        createEvents();
    }

    private void createUsers() {
        // haslo: admin
        schoolUserRepository.save(new SchoolUser("admin", "admin", "$100801$cfJJlxSl83FjJ2mh+6yUcdxxksVm3XlOzhBr4gLHFEOhdeWmaf2H6Lki/fe99YUMduDoX/LGHUcodWe9SkhVnw==$Q1yyulzoYXseBJ/OmM/xgcYD9fFPaw2bRzBHRW7RgG4=", "admin", Constants.ADMIN_ROLE));
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
        try (CSVReader reader = new CSVReader(new FileReader("src/main/resources/data/questions/questions.csv"))) {
            List<String[]> records = reader.readAll();
            for (String[] record : records) {
                Question question = new Question();
                question.setQuestion(record[0]);
                question.setAnswerA(record[1]);
                question.setAnswerB(record[2]);
                question.setAnswerC(record[3]);
                question.setCorrectAnswer(record[4]);
                question.setCategory(record[5]);
                question.setMediaName(record[6]);
                question.setQuestionType(record[7]);

//                System.out.println(question);
//                questionService.save(question);
            }
            List<Question> questions = questionService.findAll();
            for (Question question : questions) {
                System.out.println(question.getQuestion());
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public void addTeststoDb(){
        List<String> normalnames = TestNames.getNames();
        List<String> specialnames = TestNames.getSpecialtestnames();
        for(String name : normalnames){
            addTestToDb(name,false);
        }
        for(String name : specialnames){
            addTestToDb(name,true);
        }
    }
    public void addTestToDb(String Name, Boolean isSpecialistic){
        Test test = new Test();
        test.setName(Name);
        test.setCategory("B");
        test.setIsSpecialistQuestionTest(isSpecialistic);
        testService.saveTest(test);
    }

    public static byte[] convertImage(String resourcePath) throws Exception {
        ClassPathResource imgFile = new ClassPathResource(resourcePath);
        BufferedImage bImage = ImageIO.read(imgFile.getInputStream());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos);
        return bos.toByteArray();
    }

    private void createEvents() {
        SchoolUser schoolUser = schoolUserRepository.findById(1L).orElse(null);

        InstructionEvent event1 = new InstructionEvent();
        event1.setStartTime(LocalDateTime.of(2024, 5, 1, 10, 0));
        event1.setEndTime(LocalDateTime.of(2024, 5, 1, 12, 0));
        event1.setSchoolUser(schoolUser);
        event1.setStatus("Scheduled");
        event1.setEventType("Driving Lesson");

        InstructionEvent event2 = new InstructionEvent();
        event2.setStartTime(LocalDateTime.of(2024, 5, 2, 14, 0));
        event2.setEndTime(LocalDateTime.of(2024, 5, 2, 16, 0));
        event2.setSchoolUser(schoolUser);
        event2.setStatus("Completed");
        event2.setEventType("Theory Lesson");

        InstructionEvent event3 = new InstructionEvent();
        event3.setStartTime(LocalDateTime.of(2024, 5, 3, 9, 0));
        event3.setEndTime(LocalDateTime.of(2024, 5, 3, 11, 0));
        event3.setSchoolUser(schoolUser);
        event3.setStatus("Cancelled");
        event3.setEventType("Driving Lesson");

        InstructionEvent event4 = new InstructionEvent();
        event4.setStartTime(LocalDateTime.of(2024, 5, 4, 15, 0));
        event4.setEndTime(LocalDateTime.of(2024, 5, 4, 17, 0));
        event4.setSchoolUser(schoolUser);
        event4.setStatus("Scheduled");
        event4.setEventType("Driving Test");

        InstructionEvent event5 = new InstructionEvent();
        event5.setStartTime(LocalDateTime.of(2024, 5, 5, 8, 0));
        event5.setEndTime(LocalDateTime.of(2024, 5, 5, 10, 0));
        event5.setSchoolUser(schoolUser);
        event5.setStatus("Scheduled");
        event5.setEventType("Practice Session");

        InstructionEvent event6 = new InstructionEvent();
        event6.setStartTime(LocalDateTime.of(2024, 5, 6, 13, 0));
        event6.setEndTime(LocalDateTime.of(2024, 5, 6, 15, 0));
        event6.setSchoolUser(schoolUser);
        event6.setStatus("Scheduled");
        event6.setEventType("Theory Lesson");

        InstructionEvent event7 = new InstructionEvent();
        event7.setStartTime(LocalDateTime.of(2024, 5, 7, 11, 0));
        event7.setEndTime(LocalDateTime.of(2024, 5, 7, 13, 0));
        event7.setSchoolUser(schoolUser);
        event7.setStatus("Completed");
        event7.setEventType("Driving Lesson");

        InstructionEvent event8 = new InstructionEvent();
        event8.setStartTime(LocalDateTime.of(2024, 5, 8, 12, 0));
        event8.setEndTime(LocalDateTime.of(2024, 5, 8, 14, 0));
        event8.setSchoolUser(schoolUser);
        event8.setStatus("Scheduled");
        event8.setEventType("Driving Lesson");

        InstructionEvent event9 = new InstructionEvent();
        event9.setStartTime(LocalDateTime.of(2024, 5, 9, 16, 0));
        event9.setEndTime(LocalDateTime.of(2024, 5, 9, 18, 0));
        event9.setSchoolUser(schoolUser);
        event9.setStatus("Completed");
        event9.setEventType("Theory Lesson");

        InstructionEvent event10 = new InstructionEvent();
        event10.setStartTime(LocalDateTime.of(2024, 5, 10, 14, 0));
        event10.setEndTime(LocalDateTime.of(2024, 5, 10, 16, 0));
        event10.setSchoolUser(schoolUser);
        event10.setStatus("Scheduled");
        event10.setEventType("Driving Lesson");

        eventRepository.saveAll(Arrays.asList(event1, event2, event3, event4, event5, event6, event7, event8, event9, event10));
    }
}
