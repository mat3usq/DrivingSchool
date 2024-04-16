package com.driving.school.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "studentcourse")
public class Studentcourse {
    @EmbeddedId
    private StudentcourseId id;

    @MapsId("userid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userid", nullable = false)
    private User userid;

    @MapsId("courseid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "courseid", nullable = false)
    private Course courseid;

    @Column(name = "passed", precision = 10)
    private BigDecimal passed;

    @Column(name = "startedat")
    private LocalDate startedat;

    @Column(name = "endat")
    private LocalDate endat;

    public StudentcourseId getId() {
        return id;
    }

    public void setId(StudentcourseId id) {
        this.id = id;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

    public Course getCourseid() {
        return courseid;
    }

    public void setCourseid(Course courseid) {
        this.courseid = courseid;
    }

    public BigDecimal getPassed() {
        return passed;
    }

    public void setPassed(BigDecimal passed) {
        this.passed = passed;
    }

    public LocalDate getStartedat() {
        return startedat;
    }

    public void setStartedat(LocalDate startedat) {
        this.startedat = startedat;
    }

    public LocalDate getEndat() {
        return endat;
    }

    public void setEndat(LocalDate endat) {
        this.endat = endat;
    }

}