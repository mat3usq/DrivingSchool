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

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "QUESTION_ID", nullable = false)
    private Long questionId;

    @Column(name = "CATEGORY", nullable = false)
    private String category;
}
