package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "DRIVING_SESSION")
public class DrivingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "SESSION_DATE", nullable = false)
    private LocalDateTime sessionDate;

    @Column(name = "DURATION_HOURS", nullable = false)
    private Double durationHours;

    @Column(name = "INSTRUCTOR_COMMENT")
    private String instructorComment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;

    public DrivingSession(LocalDateTime sessionDate, Double durationHours, String instructorComment, Course course) {
        this.sessionDate = sessionDate;
        this.durationHours = durationHours;
        this.instructorComment = instructorComment;
        this.course = course;
    }
}