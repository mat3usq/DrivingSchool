package com.driving.school.model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "STUDENTEXAMANSWER")
public class StudentExamAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ANSWER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "STUDENTEXAMID", nullable = false)
    private StudentExam studentExam;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "QUESTIONID", nullable = false)
    private Question question;

    @Column(name = "ANSWERTYPE")
    private Long answerType;

    @Column(name = "CORRECTNESS")
    private Boolean correctness;

    @Column(name = "ANSWER")
    private String answer;

    @Override
    public String toString() {
        return "StudentExamAnswer{" +
                "id=" + id +
                ", studentExam=" + studentExam +
                ", question=" + question +
                ", answerType=" + answerType +
                ", correctness=" + correctness +
                ", answer='" + answer + '\'' +
                '}';
    }
}