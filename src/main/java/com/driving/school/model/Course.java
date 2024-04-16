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
    private Set<Studentcourse> studentcourses = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<Studentcourse> getStudentcourses() {
        return studentcourses;
    }

    public void setStudentcourses(Set<Studentcourse> studentcourses) {
        this.studentcourses = studentcourses;
    }

}