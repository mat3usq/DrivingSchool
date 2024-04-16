package com.driving.school.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "User", indexes = {
        @Index(name = "user__idx", columnList = "roleid")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 64)
    private String name;

    @Column(name = "surname", length = 64)
    private String surname;

    @Column(name = "password", length = 256)
    private String password;

    @Column(name = "email", length = 64)
    private String email;

    @Column(name = "createdat")
    private LocalDate createdat;

    @Column(name = "createdby", length = 64)
    private String createdby;

    @Column(name = "lastupdateat")
    private LocalDate lastupdateat;

    @Column(name = "lastupdatedat", length = 64)
    private String lastupdatedat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "roleid", nullable = false)
    private Role roleid;

    @OneToMany(mappedBy = "userid")
    private Set<Drivinglesson> drivinglessons = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userid")
    private Set<Lecturemeeting> lecturemeetings = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userid")
    private Set<Payment> payments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userid")
    private Set<Sensitivedatum> sensitivedata = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Studentanswerstest> studentanswerstests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Studentcourse> studentcourses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userid")
    private Set<Studentexam> studentexams = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userid")
    private Set<Userstatistic> userstatistics = new LinkedHashSet<>();

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDate createdat) {
        this.createdat = createdat;
    }

    public String getCreatedby() {
        return createdby;
    }

    public void setCreatedby(String createdby) {
        this.createdby = createdby;
    }

    public LocalDate getLastupdateat() {
        return lastupdateat;
    }

    public void setLastupdateat(LocalDate lastupdateat) {
        this.lastupdateat = lastupdateat;
    }

    public String getLastupdatedat() {
        return lastupdatedat;
    }

    public void setLastupdatedat(String lastupdatedat) {
        this.lastupdatedat = lastupdatedat;
    }

    public Role getRoleid() {
        return roleid;
    }

    public void setRoleid(Role roleid) {
        this.roleid = roleid;
    }

    public Set<Drivinglesson> getDrivinglessons() {
        return drivinglessons;
    }

    public void setDrivinglessons(Set<Drivinglesson> drivinglessons) {
        this.drivinglessons = drivinglessons;
    }

    public Set<Lecturemeeting> getLecturemeetings() {
        return lecturemeetings;
    }

    public void setLecturemeetings(Set<Lecturemeeting> lecturemeetings) {
        this.lecturemeetings = lecturemeetings;
    }

    public Set<Payment> getPayments() {
        return payments;
    }

    public void setPayments(Set<Payment> payments) {
        this.payments = payments;
    }

    public Set<Sensitivedatum> getSensitivedata() {
        return sensitivedata;
    }

    public void setSensitivedata(Set<Sensitivedatum> sensitivedata) {
        this.sensitivedata = sensitivedata;
    }

    public Set<Studentanswerstest> getStudentanswerstests() {
        return studentanswerstests;
    }

    public void setStudentanswerstests(Set<Studentanswerstest> studentanswerstests) {
        this.studentanswerstests = studentanswerstests;
    }

    public Set<Studentcourse> getStudentcourses() {
        return studentcourses;
    }

    public void setStudentcourses(Set<Studentcourse> studentcourses) {
        this.studentcourses = studentcourses;
    }

    public Set<Studentexam> getStudentexams() {
        return studentexams;
    }

    public void setStudentexams(Set<Studentexam> studentexams) {
        this.studentexams = studentexams;
    }

    public Set<Userstatistic> getUserstatistics() {
        return userstatistics;
    }

    public void setUserstatistics(Set<Userstatistic> userstatistics) {
        this.userstatistics = userstatistics;
    }

}