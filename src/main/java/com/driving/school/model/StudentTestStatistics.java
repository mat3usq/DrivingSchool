package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "STUDENTTESTSTATISTICS")
public class StudentTestStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    // ogolna ilosc odpowiedzi studenta do danego testu
    @Column(name = "NUMBEROFQUESTIONSSOLVED", nullable = false)
    private Integer numberOfQuestionsSolved;

    @Column(name = "NUMBEROFQUESTIONSANSWEREDCORRECTLY", nullable = false)
    private Integer numberOfQuestionsAnsweredCorrectly;

    @Column(name = "NUMBEROFQUESTIONSANSWEREDINCORRECTLY", nullable = false)
    private Integer numberOfQuestionsAnsweredInCorrectly;

    @Column(name = "NUMBEROFQUESTIONSSKIPPED", nullable = false)
    private Integer numberOfQuestionsSkipped;

    @Column(name = "AVERAEGEDDURATIONOFANSWERS", nullable = false)
    private Integer averageDurationOfAnswers;

    // aktualna ilosc odpowiedzi studenta do danego testu
    // (resetowana gdy uzytkownik usunie odpowiedzi do danego testu)
    @Column(name = "CURRENTNUMBEROFQUESTIONSANSWEREDCORRECTLY", nullable = false)
    private Integer currentNumberOfQuestionsAnsweredCorrectly;

    @Column(name = "CURRENTNUMBEROFQUESTIONSANSWEREDINCORRECTLY", nullable = false)
    private Integer currentNumberOfQuestionsAnsweredInCorrectly;

    @Column(name = "CURRENTNUMBEROFQUESTIONSSKIPPED", nullable = false)
    private Integer currentNumberOfQuestionsSkipped;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SCHOOLUSERID", nullable = false)
    private SchoolUser schoolUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TESTID", nullable = false)
    private Test test;
}
