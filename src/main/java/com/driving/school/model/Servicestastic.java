package com.driving.school.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "servicestastic")
public class Servicestastic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "numbersolvedexams")
    private Integer numbersolvedexams;

    @Column(name = "numberansweredquestions")
    private Integer numberansweredquestions;

    @Column(name = "numberstudent")
    private Integer numberstudent;

    @Column(name = "numbercategories")
    private Integer numbercategories;

    @Column(name = "numberinstructors")
    private Integer numberinstructors;

    @Column(name = "createdat")
    private LocalDate createdat;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumbersolvedexams() {
        return numbersolvedexams;
    }

    public void setNumbersolvedexams(Integer numbersolvedexams) {
        this.numbersolvedexams = numbersolvedexams;
    }

    public Integer getNumberansweredquestions() {
        return numberansweredquestions;
    }

    public void setNumberansweredquestions(Integer numberansweredquestions) {
        this.numberansweredquestions = numberansweredquestions;
    }

    public Integer getNumberstudent() {
        return numberstudent;
    }

    public void setNumberstudent(Integer numberstudent) {
        this.numberstudent = numberstudent;
    }

    public Integer getNumbercategories() {
        return numbercategories;
    }

    public void setNumbercategories(Integer numbercategories) {
        this.numbercategories = numbercategories;
    }

    public Integer getNumberinstructors() {
        return numberinstructors;
    }

    public void setNumberinstructors(Integer numberinstructors) {
        this.numberinstructors = numberinstructors;
    }

    public LocalDate getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDate createdat) {
        this.createdat = createdat;
    }

}