package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "USERSTATISTIC", indexes = {
        @Index(name = "USERSTATISTIC__IDX", columnList = "SCHOOLUSERID, CATEGORY_ID", unique = true)
})
public class UserStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "DEPOSITS")
    private Long deposits;

    @Column(name = "ISEXAMPASSED", length = 128)
    private String isExamPassed;

    @Column(name = "HOURSED", length = 9)
    private String hoursed;

    @Column(name = "HOURSBUYED", length = 9)
    private String hoursBuyed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SCHOOLUSERID", nullable = false)
    private SchoolUser schoolUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;
}
