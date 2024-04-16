package com.driving.school.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "userStatistic", indexes = {
        @Index(name = "userstatistic__idx", columnList = "userId")
})
public class UserStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "deposits")
    private Integer deposits;

    @Column(name = "isExamPassed", length = 128)
    private String isExamPassed;

    @Column(name = "hoursed", length = 100)
    private String hoursed;

    @Column(name = "hoursBuyed", length = 100)
    private String hoursBuyed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}