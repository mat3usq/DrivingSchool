package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "TESTSTATISTICS")
public class TestStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    // ogolna ilosc odpowiedzi wszystkich studentów zrobiona w danycm tescie
    // (do zliczania potem ogolnych statysytk w testach)
    // (odswiezane codziennie np. 24.00)
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

    @CreatedDate
    @Column(name = "CREATEDAT")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TESTID", nullable = false)
    private Test test;
}