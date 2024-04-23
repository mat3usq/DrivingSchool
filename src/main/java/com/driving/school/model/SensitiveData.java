package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "SENSITIVEDATA", indexes = {
        @Index(name = "SENSITIVEDATA__IDX", columnList = "SCHOOLUSERID", unique = true)
})
public class SensitiveData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "ADDRESS", length = 64)
    private String address;

    @Column(name = "CITY", length = 64)
    private String city;

    @Column(name = "STREETNUMBER")
    private Long streetNumber;

    @Column(name = "APARTMENTNUMBER")
    private Long apartmentNumber;

    @Column(name = "ZIPCODE", length = 64)
    private String zipCode;

    @Column(name = "PHONENUMBER", length = 15)
    private String phoneNumber;

    @Column(name = "EMAIL", length = 64)
    private String email;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "SCHOOLUSERID", nullable = false)
    private SchoolUser schoolUser;

}