package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "STUDENTEXAMANSWER")
public class StudentExamAnswer {
    @EmbeddedId
    private StudentExamAnswerId id;

    @MapsId("studentExamId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "STUDENTEXAMID", nullable = false)
    private StudentExam studentExam;

    @MapsId("questionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "QUESTIONID", nullable = false)
    private Question question;

    @Column(name = "ANSWERTYPE", nullable = false)
    private Long answerType;

}