package com.driving.school.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name = "studentanswerstest")
public class Studentanswerstest {
    @EmbeddedId
    private StudentanswerstestId id;

    @MapsId("userid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userid", nullable = false)
    private User userid;

    @MapsId("questionid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "questionid", nullable = false)
    private Question questionid;

    @MapsId("testid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "testid", nullable = false)
    private Test testid;

    @Column(name = "answertype", precision = 10)
    private BigDecimal answertype;

    public StudentanswerstestId getId() {
        return id;
    }

    public void setId(StudentanswerstestId id) {
        this.id = id;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

    public Question getQuestionid() {
        return questionid;
    }

    public void setQuestionid(Question questionid) {
        this.questionid = questionid;
    }

    public Test getTestid() {
        return testid;
    }

    public void setTestid(Test testid) {
        this.testid = testid;
    }

    public BigDecimal getAnswertype() {
        return answertype;
    }

    public void setAnswertype(BigDecimal answertype) {
        this.answertype = answertype;
    }

}