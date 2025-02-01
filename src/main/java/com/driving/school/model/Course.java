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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Table(name = "COURSE")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @NotBlank(message = "Opis nie może być pusty")
    @Size(min = 10, max = 500, message = "Opis musi mieć od 10 do 500 znaków")
    @Column(name = "DESCRIPTION")
    private String description;

    @NotNull(message = "Kategoria nie może być pusta")
    @ManyToOne
    @JoinColumn(name = "CATEGORY")
    private Category category;

    @Column(name = "PASSED")
    private String passed = Constants.COURSE_NOTSPECIFIED;

    @CreatedDate
    @Column(name = "STARTEDAT")
    private LocalDate startedAt;

    @Column(name = "ENDAT")
    private LocalDate endAt;

    @NotNull(message = "Czas trwania nie może być pusty")
    @Positive(message = "Czas trwania musi być wartością dodatnią")
    @Column(name = "DURATION")
    private Double duration;

    @Column(name = "SUMMARY_DURATION_HOURS")
    private Double summaryDurationHours = 0.0;

    @Column(name = "SUMMARY_AVERAGE_RESULT_TEST")
    private Double summaryAverageResultTest = 0.0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "MENTORSHIP_ID", nullable = false)
    private MentorShip mentorShip;

    @OrderBy("sessionDate DESC")
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DrivingSession> drivingSessions = new ArrayList<>();

    @OrderBy("testDate DESC")
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestCourse> testCourses = new ArrayList<>();

    @OrderBy("commentDate DESC")
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentCourse> commentCourses = new ArrayList<>();

    @Override
    public String toString() {
        return "Course{" +
                "description='" + description + '\'' +
                ", category=" + category +
                ", duration=" + duration +
                '}';
    }

    public Course(String description, Category category, Double duration, Double summaryDurationHours, Double summaryAverageResultTest, MentorShip mentorShip, String passed) {
        this.description = description;
        this.category = category;
        this.duration = duration;
        this.summaryDurationHours = summaryDurationHours;
        this.summaryAverageResultTest = summaryAverageResultTest;
        this.mentorShip = mentorShip;
        this.passed = passed;
    }
}