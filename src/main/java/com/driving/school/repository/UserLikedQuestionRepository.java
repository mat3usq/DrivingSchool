package com.driving.school.repository;

import com.driving.school.model.UserLikedQuestion;
import com.driving.school.model.SchoolUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLikedQuestionRepository extends JpaRepository<UserLikedQuestion, Long> {
    UserLikedQuestion findBySchoolUserAndQuestionIdAndCategory(SchoolUser schoolUser, long questionId, String category);

    void deleteBySchoolUserAndQuestionIdAndCategory(SchoolUser schoolUser, long questionId, String category);
}
