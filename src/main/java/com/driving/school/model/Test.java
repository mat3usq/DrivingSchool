package com.driving.school.model;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "test")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 512)
    private String name;

    @Column(name = "numberQuestion")
    private Integer numberQuestion;

    @Column(name = "image")
    private byte[] image;

    @Column(name = "questionType", length = 512)
    private String questionType;

    @OneToMany(mappedBy = "test")
    private Set<Question> questions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "test")
    private Set<StudentAnswersTest> studentAnswersTests = new LinkedHashSet<>();
}