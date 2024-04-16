package com.driving.school.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "sensitiveData", indexes = {
        @Index(name = "sensitivedata__idx", columnList = "userId")
})
public class SensitiveData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "address", length = 64)
    private String address;

    @Column(name = "city", length = 64)
    private String city;

    @Column(name = "streetNumber")
    private Integer streetNumber;

    @Column(name = "apartmentNumber")
    private Integer apartmentNumber;

    @Column(name = "zipCode", length = 64)
    private String zipCode;

    @Column(name = "phoneNumber", length = 15)
    private String phoneNumber;

    @Column(name = "email", length = 64)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}