package com.driving.school.model;

import jakarta.persistence.*;

import java.time.LocalDate;
@Entity
@Table(name = "blog")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "content", length = 4096)
    private String content;

    @Column(name = "image")
    private byte[] image;

    @Column(name = "createdAt")
    private LocalDate createdAt;

    @Column(name = "createdBy")
    private Integer createdBy;

    @Column(name = "lastUpdateAt")
    private LocalDate lastUpdateAt;

    @Column(name = "lastUpdateBy")
    private Integer lastUpdateBy;
}