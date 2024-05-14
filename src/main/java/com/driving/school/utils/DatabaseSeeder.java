package com.driving.school.utils;

import com.driving.school.model.Question;
import com.driving.school.service.QuestionService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private QuestionService questionService;

    @Override
    public void run(String... args) {
        try (CSVReader reader = new CSVReader(new FileReader("src/main/resources/questions/questions.csv"))) {
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



                System.out.println(question);
                //questionService.save(question);
            }
            List<Question> questions = questionService.findAll();
            for (Question question : questions) {
                System.out.println(question.getQuestion());
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

}
