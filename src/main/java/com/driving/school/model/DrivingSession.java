package com.driving.school.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "DRIVING_SESSION")
public class DrivingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @CreatedDate
    @Column(name = "SESSION_DATE")
    private LocalDateTime sessionDate;

    @NotNull(message = "Czas sesji jazdy nie może być pusty")
    @Positive(message = "Czas sesji jazdy musi być wartością dodatnią")
    @Column(name = "DURATION_HOURS")
    private Double durationHours;

    @NotBlank(message = "Komentarz nie może być pusty")
    @Size(min = 10, max = 100, message = "Komentarz musi mieć od 10 do 100 znaków")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DrivingSession that = (DrivingSession) o;
        return Objects.equals(id, that.id) && Objects.equals(sessionDate, that.sessionDate) && Objects.equals(durationHours, that.durationHours) && Objects.equals(instructorComment, that.instructorComment) && Objects.equals(course.getId(), that.course.getId());
    }
}
