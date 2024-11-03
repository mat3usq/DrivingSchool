package com.driving.school.model;

import jakarta.persistence.*;
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
@Table(name = "TEST_COURSE")
public class TestCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @CreatedDate
    @Column(name = "TEST_DATE")
    private LocalDateTime testDate;

    @Column(name = "INSTRUCTOR_COMMENT")
    private String instructorComment;

    @Column(name = "TEST_TYPE")
    private String testType = Constants.COURSE_TEST_GENERAL;

    @Column(name = "TEST_RESULT")
    private Double testResult;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COURSE_ID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;

    public TestCourse(LocalDateTime testDate, String instructorComment, String testType, Double testResult, Course course) {
        this.testDate = testDate;
        this.instructorComment = instructorComment;
        this.testType = testType;
        this.testResult = testResult;
        this.course = course;
    }
}
