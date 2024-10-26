package com.driving.school.service;

import com.driving.school.model.*;
import com.driving.school.repository.CategoryRepository;
import com.driving.school.repository.PaymentRepository;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.repository.UserLikedQuestionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SchoolUserService {
    private final SchoolUserRepository schoolUserRepository;
    private final CategoryRepository categoryRepository;
    private final UserLikedQuestionRepository userLikedQuestionRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SchoolUserService(SchoolUserRepository schoolUserRepository, CategoryRepository categoryRepository, UserLikedQuestionRepository userLikedQuestionRepository, PaymentRepository paymentRepository, PasswordEncoder passwordEncoder) {
        this.schoolUserRepository = schoolUserRepository;
        this.categoryRepository = categoryRepository;
        this.userLikedQuestionRepository = userLikedQuestionRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean createNewUser(SchoolUser user) {
        boolean isSaved = false;

        if (!schoolUserRepository.existsByEmail(user.getEmail())) {
            user.setRoleName(Constants.STUDENT_ROLE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCurrentCategory("");
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

    public Page<SchoolUser> findAllUsers(Pageable pageable) {
        return schoolUserRepository.findAll(pageable);
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
        if (user != null) {
            userLikedQuestionRepository.deleteBySchoolUserAndQuestionIdAndTestId(user, questionId, testId);
        }
    }

    public List<UserLikedQuestion> findAllLikedQuestionsByUserIdAndTestId(Long userId, Long testId) {
        return userLikedQuestionRepository.findAllBySchoolUserAndTestId(schoolUserRepository.findById(userId).orElse(null), testId);
    }

    public SchoolUser findUserByEmail(String email) {
        return schoolUserRepository.findByEmail(email);
    }

    public void changeCategory(SchoolUser user, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);

        if (category != null && user.getAvailableCategories().contains(category)) {
            user.setCurrentCategory(category.getNameCategory());
            schoolUserRepository.save(user);
        }
    }

    public void deletePayment(Long paymentId) {
        Optional<Payment> payment = paymentRepository.findById(paymentId);
        if (payment.isPresent()) {
            Payment pay = payment.get();
            Optional<SchoolUser> user = schoolUserRepository.findById(pay.getSchoolUser().getId());

            if (user.isPresent()) {
                SchoolUser schoolUser = user.get();

                List<Category> categories = schoolUser.getAvailableCategories();
                categories.removeAll(pay.getCategories());
                schoolUser.setAvailableCategories(categories);
                pay.getCategories().forEach(c -> {
                    if (c.getNameCategory().equals(schoolUser.getCurrentCategory()))
                        schoolUser.setCurrentCategory("");
                });

                paymentRepository.delete(pay);
                schoolUserRepository.save(schoolUser);
            }
        }
    }
}