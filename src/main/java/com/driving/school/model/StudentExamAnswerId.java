package com.driving.school.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class StudentExamAnswerId implements Serializable {
    private static final long serialVersionUID = -720916240282342060L;
    @Column(name = "STUDENTEXAMID", nullable = false)
    private Long studentExamId;

    @Column(name = "QUESTIONID", nullable = false)
    private Long questionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudentExamAnswerId entity = (StudentExamAnswerId) o;
        return Objects.equals(this.questionId, entity.questionId) &&
                Objects.equals(this.studentExamId, entity.studentExamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, studentExamId);
    }

}