package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "USER_LIKED_QUESTIONS")
@Getter
@Setter
public class UserLikedQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    private SchoolUser schoolUser;

    @Column(name = "QUESTION_ID")
    private Long questionId;

    @Column(name = "TEST_ID")
    private Long testId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLikedQuestion that = (UserLikedQuestion) o;
        return Objects.equals(schoolUser.getId(), that.schoolUser.getId()) && Objects.equals(questionId, that.questionId) && Objects.equals(testId, that.testId);
    }
}
