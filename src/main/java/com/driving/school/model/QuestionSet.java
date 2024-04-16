package com.driving.school.model;

import jakarta.persistence.*;

@Entity
@Table(name = "questionSet")
public class QuestionSet {
    @EmbeddedId
    private QuestionSetId id;

    @MapsId("examId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "examId", nullable = false)
    private Exam exam;

    @MapsId("questionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;
}