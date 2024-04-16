package com.driving.school.model;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "question", length = 512)
    private String question;

    @Column(name = "answerA", length = 512)
    private String answerA;

    @Column(name = "answerB", length = 512)
    private String answerB;

    @Column(name = "answerC", length = 512)
    private String answerC;

    @Column(name = "availableQuestions")
    private Integer availableQuestions;

    @Column(name = "correctAnswer")
    private Integer correctAnswer;

    @Column(name = "mediaName", length = 512)
    private String mediaName;

    @Column(name = "explanation", length = 1024)
    private String explanation;

    @Column(name = "questionType", length = 512)
    private String questionType;

    @Column(name = "category", length = 128)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "testId", nullable = false)
    private Test test;

    @ManyToMany(mappedBy = "questions")
    private Set<Exam> exams = new LinkedHashSet<>();

    @OneToMany(mappedBy = "question")
    private Set<StudentAnswersTest> studentAnswersTests = new LinkedHashSet<>();

    @OneToOne(mappedBy = "question")
    private StudentExamAnswer studentExamAnswer;
}