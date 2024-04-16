package com.driving.school.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contact")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "content", length = 2048)
    private String content;

    @Column(name = "name", length = 128)
    private String name;

    @Column(name = "surname", length = 128)
    private String surname;

    @Column(name = "phone", length = 12)
    private String phone;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "status", length = 128)
    private String status;
}