package com.driving.school.utils;

import com.driving.school.model.*;
import com.driving.school.repository.LectureRepository;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.repository.SubjectRepository;
import com.driving.school.repository.SublectureRepository;
import com.driving.school.service.LectureService;
import com.driving.school.service.QuestionService;
import com.driving.school.service.SchoolUserService;
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

    @Autowired
    public DatabaseSeeder(QuestionService questionService, SchoolUserRepository schoolUserRepository, LectureRepository lectureRepository, SublectureRepository sublectureRepository, SubjectRepository subjectRepository) {
        this.questionService = questionService;
        this.schoolUserRepository = schoolUserRepository;
        this.lectureRepository = lectureRepository;
        this.sublectureRepository = sublectureRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public void run(String... args) {
        mapQuestionsToDb();
        createUsers();
        createLectures();
    }

    private void createUsers() {
        // haslo: admin
        schoolUserRepository.save(new SchoolUser("admin", "admin", "$100801$cfJJlxSl83FjJ2mh+6yUcdxxksVm3XlOzhBr4gLHFEOhdeWmaf2H6Lki/fe99YUMduDoX/LGHUcodWe9SkhVnw==$Q1yyulzoYXseBJ/OmM/xgcYD9fFPaw2bRzBHRW7RgG4=", "admin", Constants.ADMIN_ROLE));
    }

    private void createLectures() {
        try {
            Lecture l = new Lecture("RUCH POJAZDOW", null, 1);
            Sublecture sl1 = new Sublecture("Ogólne zasady ruchu pojazdów.", "Kierującego pojazdem obowiązuje ruch prawostronny. Odwołując się do Kodeksu Ruchu Drogowego (rozdział 3, oddział 1, art. 16) „Kierujący pojazdem, korzystając z drogi dwujezdniowej, jest obowiązany jechać po prawej jezdni; do jezdni tych nie wlicza się jezdni przeznaczonej do dojazdu do nieruchomości położonej przy drodze”.", 1, l);
            Sublecture sl2 = new Sublecture("Włączanie się do ruchu.", "Włączanie się do ruchu wymaga zachowania szczególnej ostrożności, a także konieczności ustąpienia pierwszeństwa wszystkim pozostałym uczestnikom ruchu. Przede wszystkim, zanim rozpoczniesz ruch, dokonaj uważnej obserwacji sytuacji jaka występuje na drodze, a także podejmij trafną decyzję, czy manewr włączania się do ruchu przeprowadzisz w bezpieczny sposób. Jeżeli masz jakiekolwiek wątpliwości zaczekaj na poprawę sytuacji.", 2, l);
            List<Subject> subjects = new ArrayList<>(Arrays.asList(
                    new Subject(null, "Odwołując się do Kodeksu Ruchu Drogowego (rozdział 3, oddział 1, art. 16) „Kierujący pojazdem, korzystając z jezdni dwukierunkowej co najmniej o czterech pasach ruchu, jest obowiązany zajmować pas ruchu znajdujący się na prawej połowie jezdni”",
                            convertImage("/data/image/droga-dwujezdniowa.jpg"), 1, sl1),
                    new Subject(null,
                            "„Kierujący pojazdem jest obowiązany jechać możliwie blisko prawej krawędzi jezdni. Jeżeli pasy ruchu na jezdni są wyznaczone, nie może zajmować więcej niż jednego pasa.”",
                            convertImage("/data/image/zajmowanie-jednego-pasa-ruchu.jpg"), 2, sl1),
                    new Subject("Włączającym się do ruchu jesteś także w ściśle określonych przepisami sytuacjach, np. podczas:",
                            null, null, 1, sl2),
                    new Subject("1. wyjazdu ze strefy zamieszkania,",
                            null,
                            convertImage("/data/image/wyjazd-ze-strefy-zamieszkania.jpg"), 2, sl2),
                    new Subject("2. wyjazdu na drogę z nieruchomości, obiektu przydrożnego tj. parkingu, stacji benzynowej,",
                            null,
                            convertImage("/data/image/wyjazd-z-parkingu.jpg"), 3, sl2),
                    new Subject("3. wyjazdu z drogi niebędącej drogą publiczną,",
                            null,
                            convertImage("/data/image/wyjazd-z-drogi-niebedacej-droga-publiczna.jpg"), 4, sl2),
                    new Subject("4. wyjazdu na drogę z pola,",
                            null,
                            convertImage("/data/image/wyjazd-z-drogi-niebedacej-droga-publiczna.jpg"), 5, sl2),
                    new Subject("5. wyjazdu z drogi niebędącej drogą publiczną (drogi wewnętrznej),",
                            null,
                            convertImage("/data/image/wyjazd-z-drogi-wewnetrznej.jpg"), 6, sl2),
                    new Subject("6. wyjazdu z drogi dla rowerów na jezdnię lub pobocze, z wyjątkiem wjazdu na przejazd dla rowerzystów lub pas ruchu dla rowerów.",
                            null,
                            convertImage("/data/image/wyjazd-z-drogi-dla-rowerow.jpg"), 7, sl2)
            ));
            lectureRepository.save(l);
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

    public static byte[] convertImage(String resourcePath) throws Exception {
        ClassPathResource imgFile = new ClassPathResource(resourcePath);
        BufferedImage bImage = ImageIO.read(imgFile.getInputStream());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos);
        return bos.toByteArray();
    }
}
