package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "DRIVINGLESSON")
public class DrivingLesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "STARTTIME")
    private LocalDate startTime;

    @Column(name = "ENDTIME")
    private LocalDate endTime;

    @Column(name = "TIME", length = 9)
    private String time;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "SCHOOLUSERID", nullable = false)
    private SchoolUser schoolUser;

    @Column(name = "STUDENTID")
    private Long studentId;

    @Column(name = "STATUS", length = 128)
    private String status;

}