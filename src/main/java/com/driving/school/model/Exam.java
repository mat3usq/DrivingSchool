package com.driving.school.model;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "exam")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 128)
    private String name;

    @Column(name = "points")
    private Integer points;

    @Column(name = "questionNumber")
    private Integer questionNumber;

    @ManyToMany
    @JoinTable(name = "questionSet",
            joinColumns = @JoinColumn(name = "examId"),
            inverseJoinColumns = @JoinColumn(name = "questionId"))
    private Set<Question> questions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "exam")
    private Set<StudentExam> studentExams = new LinkedHashSet<>();
}