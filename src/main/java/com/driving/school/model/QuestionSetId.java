package com.driving.school.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class QuestionSetId implements Serializable {
    private static final long serialVersionUID = -5673347618906998319L;
    @Column(name = "examId", nullable = false)
    private Integer examId;

    @Column(name = "questionId", nullable = false)
    private Integer questionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        QuestionSetId entity = (QuestionSetId) o;
        return Objects.equals(this.questionId, entity.questionId) &&
                Objects.equals(this.examId, entity.examId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, examId);
    }
}