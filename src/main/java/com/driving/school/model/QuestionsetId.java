package com.driving.school.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class QuestionsetId implements Serializable {
    private static final long serialVersionUID = -5673347618906998319L;
    @Column(name = "examid", nullable = false)
    private Integer examid;

    @Column(name = "questionid", nullable = false)
    private Integer questionid;

    public Integer getExamid() {
        return examid;
    }

    public void setExamid(Integer examid) {
        this.examid = examid;
    }

    public Integer getQuestionid() {
        return questionid;
    }

    public void setQuestionid(Integer questionid) {
        this.questionid = questionid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        QuestionsetId entity = (QuestionsetId) o;
        return Objects.equals(this.questionid, entity.questionid) &&
                Objects.equals(this.examid, entity.examid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionid, examid);
    }

}