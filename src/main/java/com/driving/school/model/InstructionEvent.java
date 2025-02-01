package com.driving.school.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Temat jest wymagany")
    @Size(max = 100, message = "Temat może mieć maksymalnie 100 znaków")
    @Column(name = "SUBJECT", length = 100)
    private String subject;

    @NotBlank(message = "Typ wydarzenia jest wymagany")
    @Size(max = 100, message = "Typ wydarzenia może mieć maksymalnie 100 znaków")
    @Column(name = "EVENTTYPE", length = 100)
    private String eventType;

    @NotNull(message = "Czas rozpoczęcia jest wymagany")
    @Column(name = "STARTTIME")
    private LocalDateTime startTime;

    @NotNull(message = "Czas zakończenia jest wymagany")
    @Column(name = "ENDTIME")
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "INSTRUCTORID")
    private SchoolUser instructor;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "EVENTSTUDENTS",
            joinColumns = @JoinColumn(name = "EVENTID"),
            inverseJoinColumns = @JoinColumn(name = "STUDENTID")
    )
    private List<SchoolUser> students;

    @Column(name = "STATUS", length = 64)
    private String status;

    @NotNull(message = "Liczba miejsc na wydarzenie jest wymagane")
    @Min(value = 1, message = "Liczba miejsc na wydarzenie musi byc co najmniej 1")
    @Column(name = "EVENTCAPACITY")
    private Integer eventCapacity;

    @Column(name = "AVAILABLEEVENTSLOTS")
    private Integer availableEventSlots;

    @Transient
    private Boolean isAssigned;

    @AssertTrue(message = "Czas rozpoczęcia musi być przed czasem zakończenia")
    public boolean isStartTimeBeforeEndTime() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return !startTime.isAfter(endTime);
    }

    public InstructionEvent(String subject, String eventType, LocalDateTime startTime, LocalDateTime endTime, SchoolUser instructor, Integer eventCapacity) {
        this.subject = subject;
        this.eventType = eventType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.instructor = instructor;
        this.eventCapacity = eventCapacity;
        this.availableEventSlots = eventCapacity;
    }

    @Override
    public String toString() {
        return "InstructionEvent{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", eventType='" + eventType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", instructor=" + instructor +
                ", students=" + students +
                ", status='" + status + '\'' +
                ", eventCapacity=" + eventCapacity +
                ", availableEventSlots=" + availableEventSlots +
                ", isAssigned=" + isAssigned +
                '}';
    }
}
