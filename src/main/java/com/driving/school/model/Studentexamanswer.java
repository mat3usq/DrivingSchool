package com.driving.school.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name = "studentexamanswer")
public class Studentexamanswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionid", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "questionid", nullable = false)
    private Question question;

    @Column(name = "answertype", precision = 10)
    private BigDecimal answertype;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "studentexamid", nullable = false)
    private Studentexam studentexamid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public BigDecimal getAnswertype() {
        return answertype;
    }

    public void setAnswertype(BigDecimal answertype) {
        this.answertype = answertype;
    }

    public Studentexam getStudentexamid() {
        return studentexamid;
    }

    public void setStudentexamid(Studentexam studentexamid) {
        this.studentexamid = studentexamid;
    }

}