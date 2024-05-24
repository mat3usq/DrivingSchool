package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "INSTRUCTIONEVENTS")
public class InstructionEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "SUBJECT", length = 128)
    private String subject;

    @Column(name = "EVENTTYPE", length = 128)
    private String eventType;

    @Column(name = "STARTTIME")
    private LocalDateTime startTime;

    @Column(name = "ENDTIME")
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "SCHOOLUSERID", nullable = false)
    private SchoolUser schoolUser;

    @Column(name = "STATUS", length = 64)
    private String status;
}