package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "CONTACT")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "CONTENT", length = 2048)
    private String content;

    @Column(name = "NAME", length = 128)
    private String name;

    @Column(name = "SURNAME", length = 128)
    private String surname;

    @Column(name = "PHONE", length = 12)
    private String phone;

    @Column(name = "EMAIL", length = 128)
    private String email;

    @Column(name = "STATUS", length = 128)
    private String status;

}