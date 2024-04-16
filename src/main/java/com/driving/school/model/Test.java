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

    @Column(name = "numberquestion")
    private Integer numberquestion;

    @Column(name = "image")
    private byte[] image;

    @Column(name = "questiontype", length = 512)
    private String questiontype;

    @OneToMany(mappedBy = "testid")
    private Set<Question> questions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "test")
    private Set<Studentanswerstest> studentanswerstests = new LinkedHashSet<>();

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

    public Integer getNumberquestion() {
        return numberquestion;
    }

    public void setNumberquestion(Integer numberquestion) {
        this.numberquestion = numberquestion;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getQuestiontype() {
        return questiontype;
    }

    public void setQuestiontype(String questiontype) {
        this.questiontype = questiontype;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public Set<Studentanswerstest> getStudentanswerstests() {
        return studentanswerstests;
    }

    public void setStudentanswerstests(Set<Studentanswerstest> studentanswerstests) {
        this.studentanswerstests = studentanswerstests;
    }

}