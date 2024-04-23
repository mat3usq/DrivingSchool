package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "TEST")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", length = 512)
    private String name;

    @Column(name = "NUMBERQUESTIONS")
    private Long numberQuestions;

    @Column(name = "IMAGE")
    private byte[] image;

    @Column(name = "QUESTIONTYPE", length = 512)
    private String questionType;

    @OneToMany(mappedBy = "test")
    private Set<Question> questions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "test")
    private Set<StudentAnswersTest> studentAnswersTests = new LinkedHashSet<>();

}