package com.driving.school.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "STUDENTTESTSTATISTICS")
public class StudentTestStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    // ogolna ilosc odpowiedzi studenta do danego testu
    @Column(name = "NUMBEROFQUESTIONSSOLVED", nullable = false)
    private int numberOfQuestionsSolved;

    @Column(name = "NUMBEROFQUESTIONSANSWEREDCORRECTLY", nullable = false)
    private int numberOfQuestionsAnsweredCorrectly;

    @Column(name = "NUMBEROFQUESTIONSANSWEREDINCORRECTLY", nullable = false)
    private int numberOfQuestionsAnsweredInCorrectly;

    @Column(name = "NUMBEROFQUESTIONSSKIPPED", nullable = false)
    private int numberOfQuestionsSkipped;

    @Column(name = "AVERAGEDURATIONOFANSWERS", nullable = false)
    private Double averageDurationOfAnswers;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SCHOOLUSERID", nullable = false)
    private SchoolUser schoolUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TESTID", nullable = false)
    private Test test;

    @Override
    public String toString() {
        return "StudentTestStatistics{" +
                "id=" + id +
                ", numberOfQuestionsSolved=" + numberOfQuestionsSolved +
                ", numberOfQuestionsAnsweredCorrectly=" + numberOfQuestionsAnsweredCorrectly +
                ", numberOfQuestionsAnsweredInCorrectly=" + numberOfQuestionsAnsweredInCorrectly +
                ", numberOfQuestionsSkipped=" + numberOfQuestionsSkipped +
                ", averageDurationOfAnswers=" + averageDurationOfAnswers +
                ", schoolUser=" + schoolUser +
                ", test=" + test +
                '}';
    }
}
