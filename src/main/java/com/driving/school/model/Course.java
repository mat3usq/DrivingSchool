package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
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
@Table(name = "COURSE")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "DESCRIPTION")
    private String description;

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
}