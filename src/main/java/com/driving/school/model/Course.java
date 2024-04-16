package com.driving.school.model;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 64)
    private String name;

    @Column(name = "description", length = 64)
    private String description;

    @Column(name = "duration", length = 100)
    private String duration;

    @Column(name = "category", length = 64)
    private String category;

    @OneToMany(mappedBy = "course")
    private Set<StudentCourse> studentCours = new LinkedHashSet<>();
}