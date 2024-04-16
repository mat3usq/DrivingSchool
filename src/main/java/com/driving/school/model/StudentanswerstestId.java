package com.driving.school.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StudentanswerstestId implements Serializable {
    private static final long serialVersionUID = 2447120928541774612L;
    @Column(name = "userid", nullable = false)
    private Integer userid;

    @Column(name = "questionid", nullable = false)
    private Integer questionid;

    @Column(name = "testid", nullable = false)
    private Integer testid;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getQuestionid() {
        return questionid;
    }

    public void setQuestionid(Integer questionid) {
        this.questionid = questionid;
    }

    public Integer getTestid() {
        return testid;
    }

    public void setTestid(Integer testid) {
        this.testid = testid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudentanswerstestId entity = (StudentanswerstestId) o;
        return Objects.equals(this.questionid, entity.questionid) &&
                Objects.equals(this.testid, entity.testid) &&
                Objects.equals(this.userid, entity.userid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionid, testid, userid);
    }

}