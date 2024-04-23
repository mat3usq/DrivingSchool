package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "EXAM")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", length = 128)
    private String name;

    @Column(name = "POINTS")
    private Long points;

    @Column(name = "QUESTIONNUMBER")
    private Long questionNumber;

    @ManyToMany
    @JoinTable(name = "QUESTIONSET",
            joinColumns = @JoinColumn(name = "EXAMID"),
            inverseJoinColumns = @JoinColumn(name = "QUESTIONID"))
    private Set<Question> questions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "exam")
    private Set<StudentExam> studentExams = new LinkedHashSet<>();

}