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

    @Column(name = "questionnumber")
    private Integer questionnumber;

    @ManyToMany
    @JoinTable(name = "questionset",
            joinColumns = @JoinColumn(name = "examid"),
            inverseJoinColumns = @JoinColumn(name = "questionid"))
    private Set<Question> questions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "examid")
    private Set<Studentexam> studentexams = new LinkedHashSet<>();

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

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getQuestionnumber() {
        return questionnumber;
    }

    public void setQuestionnumber(Integer questionnumber) {
        this.questionnumber = questionnumber;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public Set<Studentexam> getStudentexams() {
        return studentexams;
    }

    public void setStudentexams(Set<Studentexam> studentexams) {
        this.studentexams = studentexams;
    }

}