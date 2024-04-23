package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "COURSE")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", length = 64)
    private String name;

    @Column(name = "DESCRIPTION", length = 64)
    private String description;

    @Column(name = "DURATION", length = 9)
    private String duration;

    @Column(name = "CATEGORY", length = 64)
    private String category;

    @OneToMany(mappedBy = "course")
    private Set<StudentCourse> studentCourses = new LinkedHashSet<>();

}