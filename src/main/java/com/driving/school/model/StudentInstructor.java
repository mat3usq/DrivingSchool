package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "STUDENTINSTRUCTOR")
public class StudentInstructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @MapsId("studentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "STUDENTID", nullable = false)
    private SchoolUser student;

    @MapsId("instructorId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "INSTRUCTORID", nullable = false)
    private SchoolUser instructor;

    @Column(name = "STARTAT")
    private LocalDateTime startedAt;

    @Column(name = "ENDAT")
    private LocalDateTime endAt;

    @Column(name = "STATUS", length = 128)
    private String status;
}