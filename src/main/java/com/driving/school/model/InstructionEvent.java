package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
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

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "SCHOOLUSERID")
    private SchoolUser schoolUser;

    public InstructionEvent(String subject, String eventType, LocalDateTime startTime, LocalDateTime endTime, SchoolUser schoolUser) {
        this.subject = subject;
        this.eventType = eventType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.schoolUser = schoolUser;
    }
}