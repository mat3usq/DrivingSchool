package com.driving.school.repository;

import com.driving.school.model.UserLikedQuestion;
import com.driving.school.model.SchoolUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLikedQuestionRepository extends JpaRepository<UserLikedQuestion, Long> {
    UserLikedQuestion findBySchoolUserAndQuestionIdAndTestId(SchoolUser schoolUser, long questionId, long testId);

    void deleteBySchoolUserAndQuestionIdAndTestId(SchoolUser schoolUser, long questionId, long testId);

    List<UserLikedQuestion> findAllBySchoolUserAndTestId(SchoolUser schoolUser, long testId);
}
