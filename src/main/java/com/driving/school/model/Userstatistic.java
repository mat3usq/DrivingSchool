package com.driving.school.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "userstatistic", indexes = {
        @Index(name = "userstatistic__idx", columnList = "userid")
})
public class Userstatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "deposits")
    private Integer deposits;

    @Column(name = "isexampassed", length = 128)
    private String isexampassed;

    @Column(name = "hourused", length = 100)
    private String hourused;

    @Column(name = "hoursbuyed", length = 100)
    private String hoursbuyed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userid", nullable = false)
    private User userid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeposits() {
        return deposits;
    }

    public void setDeposits(Integer deposits) {
        this.deposits = deposits;
    }

    public String getIsexampassed() {
        return isexampassed;
    }

    public void setIsexampassed(String isexampassed) {
        this.isexampassed = isexampassed;
    }

    public String getHourused() {
        return hourused;
    }

    public void setHourused(String hourused) {
        this.hourused = hourused;
    }

    public String getHoursbuyed() {
        return hoursbuyed;
    }

    public void setHoursbuyed(String hoursbuyed) {
        this.hoursbuyed = hoursbuyed;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

}