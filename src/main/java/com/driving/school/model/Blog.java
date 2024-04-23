package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "BLOG")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "CONTENT", length = 3800)
    private String content;

    @Column(name = "IMAGE")
    private byte[] image;

    @Column(name = "CREATEDAT")
    private LocalDateTime createdAt;

    @Column(name = "CREATEDBY")
    private Long createdBy;

    @Column(name = "LASTUPDATEDAT")
    private LocalDateTime lastUpdatedAt;

    @Column(name = "LASTUPDATEDBY")
    private Long lastUpdatedBy;

}