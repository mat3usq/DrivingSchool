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
public class QuestionSetId implements Serializable {
    private static final long serialVersionUID = -1746765468589445033L;
    @Column(name = "EXAMID", nullable = false)
    private Long examid;

    @Column(name = "QUESTIONID", nullable = false)
    private Long questionid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        QuestionSetId entity = (QuestionSetId) o;
        return Objects.equals(this.questionid, entity.questionid) &&
                Objects.equals(this.examid, entity.examid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionid, examid);
    }

}