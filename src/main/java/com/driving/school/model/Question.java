package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "QUESTION")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "QUESTION", length = 512)
    private String question;

    @Column(name = "ANSWERA", length = 512)
    private String answerA;

    @Column(name = "ANSWERB", length = 512)
    private String answerB;

    @Column(name = "ANSWERC", length = 512)
    private String answerC;

    @Column(name = "AVAILABLEQUESTIONS")
    private Long availableQuestions;

    @Column(name = "CORRECTANSWER")
    private Long correctAnswer;

    @Column(name = "MEDIANAME", length = 512)
    private String mediaName;

    @Column(name = "EXPLANATION", length = 1024)
    private String explanation;

    @Column(name = "QUESTIONTYPE", length = 512)
    private String questionType;

    @Column(name = "CATEGORY", length = 128)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "TESTID", nullable = false)
    private Test test;

    @ManyToMany(mappedBy = "questions")
    private Set<Exam> exams = new LinkedHashSet<>();

    @OneToMany(mappedBy = "question")
    private Set<StudentAnswersTest> studentAnswersTests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "question")
    private Set<StudentExamAnswer> studentExamAnswers = new LinkedHashSet<>();

}