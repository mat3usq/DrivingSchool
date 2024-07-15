package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "USER_LIKED_QUESTIONS")
@Getter
@Setter
public class UserLikedQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private SchoolUser schoolUser;

    @Column(name = "QUESTION_ID", nullable = false)
    private Long questionId;

    @Column(name = "TEST_ID", nullable = false)
    private Long testId;
}
