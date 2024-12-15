package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "STUDENTANSWERSTEST")
public class StudentAnswersTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "CORRECTNESS")
    private Boolean correctness;

    @Column(name = "SKIPPED")
    private Boolean skipped;

    @Column(name = "DURATIONOFANSWER", nullable = false)
    private Long durationOfAnswer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "SCHOOLUSERID", nullable = false)
    private SchoolUser schoolUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "QUESTIONID", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "TESTID", nullable = false)
    private Test test;

    @Override
    public String toString() {
        return "StudentAnswersTest{" +
                "id=" + id +
                ", correctness=" + correctness +
                ", skipped=" + skipped +
                ", durationOfAnswer=" + durationOfAnswer +
                ", question=" + question.getId() +
                ", test=" + test.getId() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentAnswersTest that = (StudentAnswersTest) o;
        return Objects.equals(schoolUser.getId(), that.schoolUser.getId()) && Objects.equals(question.getId(), that.question.getId()) && Objects.equals(test.getId(), that.test.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(schoolUser.getId(), question.getId(), test.getId());
    }
}