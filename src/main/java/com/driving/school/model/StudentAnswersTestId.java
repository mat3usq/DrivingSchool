package com.driving.school.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StudentAnswersTestId implements Serializable {
    private static final long serialVersionUID = 2447120928541774612L;
    @Column(name = "userId", nullable = false)
    private Integer userId;

    @Column(name = "questionId", nullable = false)
    private Integer questionId;

    @Column(name = "testId", nullable = false)
    private Integer testId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudentAnswersTestId entity = (StudentAnswersTestId) o;
        return Objects.equals(this.questionId, entity.questionId) &&
                Objects.equals(this.testId, entity.testId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, testId, userId);
    }
}