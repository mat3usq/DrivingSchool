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

    @Column(name = "AVAILABLEANSWERS")
    private Long availableAnswers;

    @Column(name = "CORRECTANSWER")
    private String correctAnswer;

    @Column(name = "MEDIANAME", length = 512)
    private String mediaName;

    @Column(name = "EXPLANATION", length = 1024)
    private String explanation;

    @Column(name = "QUESTIONTYPE", length = 512)
    private Boolean questionType;

    @Column(name = "CATEGORY", length = 128)
    private String drivingCategory;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "TESTID", nullable = true)
    private Test test;

    @ManyToMany(mappedBy = "questions")
    private Set<Exam> exams = new LinkedHashSet<>();

    @OneToMany(mappedBy = "question")
    private Set<StudentAnswersTest> studentAnswersTests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "question")
    private Set<StudentExamAnswer> studentExamAnswers = new LinkedHashSet<>();

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answerA='" + answerA + '\'' +
                ", answerB='" + answerB + '\'' +
                ", answerC='" + answerC + '\'' +
                ", availableAnswers=" + availableAnswers +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", mediaName='" + mediaName + '\'' +
                ", explanation='" + explanation + '\'' +
                ", questionType='" + questionType + '\'' +
                ", category='" + drivingCategory + '\'' +
                ", test=" + test +
                ", exams=" + exams +
                ", studentAnswersTests=" + studentAnswersTests +
                ", studentExamAnswers=" + studentExamAnswers +
                '}';
    }
}