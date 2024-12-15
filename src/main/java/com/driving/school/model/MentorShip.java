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
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "MENTORSHIP")
@EntityListeners(AuditingEntityListener.class)
public class MentorShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "STUDENTID", nullable = false)
    private SchoolUser student;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "INSTRUCTORID", nullable = false)
    private SchoolUser instructor;

    @CreatedDate
    @Column(name = "STARTAT")
    private LocalDateTime startedAt;

    @Column(name = "ENDAT")
    private LocalDateTime endAt;

    @Column(name = "STATUS", length = 128)
    private String status;

    @OneToMany(mappedBy = "mentorShip")
    private Set<Course> studentCourses = new LinkedHashSet<>();

    public MentorShip(SchoolUser student, SchoolUser instructor, String status) {
        this.student = student;
        this.instructor = instructor;
        this.status = status;
    }
}