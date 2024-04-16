package com.driving.school.model;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "lecture")
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 128)
    private String name;

    @OneToMany(mappedBy = "lectureid")
    private Set<Sublecture> sublectures = new LinkedHashSet<>();

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

    public Set<Sublecture> getSublectures() {
        return sublectures;
    }

    public void setSublectures(Set<Sublecture> sublectures) {
        this.sublectures = sublectures;
    }

}