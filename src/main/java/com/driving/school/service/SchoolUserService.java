package com.driving.school.service;

import com.driving.school.model.Constants;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.StudentAnswersTest;
import com.driving.school.model.UserLikedQuestion;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.repository.UserLikedQuestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SchoolUserService {
    private final SchoolUserRepository schoolUserRepository;
    private final UserLikedQuestionRepository userLikedQuestionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SchoolUserService(SchoolUserRepository schoolUserRepository, UserLikedQuestionRepository userLikedQuestionRepository, PasswordEncoder passwordEncoder) {
        this.schoolUserRepository = schoolUserRepository;
        this.userLikedQuestionRepository = userLikedQuestionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean createNewUser(SchoolUser user) {
        boolean isSaved = false;

        if (!schoolUserRepository.existsByEmail(user.getEmail())) {
            user.setRoleName(Constants.STUDENT_ROLE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCurrentCategory("Brak Kategorii");
            user = schoolUserRepository.save(user);
            if (user.getId() > 0)
                isSaved = true;
        }

        return isSaved;
    }

    public SchoolUser findUserById(Long id) {
        return schoolUserRepository.findById(id).orElse(null);
    }

    public List<SchoolUser> findAllInstructors() {
        return schoolUserRepository.findAll().stream().filter(u -> u.getRoleName().equals(Constants.INSTRUCTOR_ROLE)).toList();
    }

    @Transactional
    public void addLikedQuestionToUser(Long questionId, Long testId, SchoolUser usr) {
        SchoolUser user = findUserById(usr.getId());
        UserLikedQuestion existingRecord = userLikedQuestionRepository.findBySchoolUserAndQuestionIdAndTestId(user, questionId, testId);

        if (user != null && existingRecord == null) {
            UserLikedQuestion userLikedQuestion = new UserLikedQuestion();
            userLikedQuestion.setSchoolUser(user);
            userLikedQuestion.setQuestionId(questionId);
            userLikedQuestion.setTestId(testId);
            user.getLikedQuestions().add(userLikedQuestion);
            user.setLikedQuestions(user.getLikedQuestions());
            schoolUserRepository.save(user);
        }
    }

    @Transactional
    public void deleteLikedQuestionFromUser(Long questionId, Long testId, SchoolUser user) {
        if (user != null){
            userLikedQuestionRepository.deleteBySchoolUserAndQuestionIdAndTestId(user, questionId, testId);
            schoolUserRepository.save(user);
        }
    }

    public List<UserLikedQuestion> findAllLikedQuestionsByUserIdAndTestId(Long userId, Long testId) {
        return userLikedQuestionRepository.findAllBySchoolUserAndTestId(schoolUserRepository.findById(userId).orElse(null), testId);
    }
}