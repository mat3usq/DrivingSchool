package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

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
    @JoinColumn(name = "INSTRUCTORID")
    private SchoolUser instructor;

    @ManyToMany
    @JoinTable(
            name = "EVENTSTUDENTS",
            joinColumns = @JoinColumn(name = "EVENTID"),
            inverseJoinColumns = @JoinColumn(name = "STUDENTID")
    )
    private List<SchoolUser> students;

    @Column(name = "STATUS", length = 64)
    private String status;

    @Column(name = "EVENTCAPACITY")
    private Integer eventCapacity;

    @Column(name = "AVAILABLEEVENTSLOTS")
    private Integer availableEventSlots;

    @Transient
    private Boolean isAssigned;

    public InstructionEvent(String subject, String eventType, LocalDateTime startTime, LocalDateTime endTime, SchoolUser instructor, Integer eventCapacity) {
        this.subject = subject;
        this.eventType = eventType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.instructor = instructor;
        this.eventCapacity = eventCapacity;
        this.availableEventSlots = eventCapacity;
    }
}
