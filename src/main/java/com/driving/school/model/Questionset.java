package com.driving.school.model;

import jakarta.persistence.*;

@Entity
@Table(name = "questionset")
public class Questionset {
    @EmbeddedId
    private QuestionsetId id;

    @MapsId("examid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "examid", nullable = false)
    private Exam examid;

    @MapsId("questionid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "questionid", nullable = false)
    private Question questionid;

    public QuestionsetId getId() {
        return id;
    }

    public void setId(QuestionsetId id) {
        this.id = id;
    }

    public Exam getExamid() {
        return examid;
    }

    public void setExamid(Exam examid) {
        this.examid = examid;
    }

    public Question getQuestionid() {
        return questionid;
    }

    public void setQuestionid(Question questionid) {
        this.questionid = questionid;
    }

}