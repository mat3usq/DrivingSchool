package com.driving.school.service;

import com.driving.school.model.*;
import com.driving.school.repository.CategoryRepository;
import com.driving.school.repository.PaymentRepository;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.repository.UserLikedQuestionRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

@Service
public class SchoolUserService {
    private final SchoolUserRepository schoolUserRepository;
    private final CategoryRepository categoryRepository;
    private final UserLikedQuestionRepository userLikedQuestionRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    private final NotificationService notificationService;

    @Autowired
    public SchoolUserService(SchoolUserRepository schoolUserRepository, CategoryRepository categoryRepository, UserLikedQuestionRepository userLikedQuestionRepository, PaymentRepository paymentRepository, PasswordEncoder passwordEncoder, EmailSenderService emailSenderService, NotificationService notificationService) {
        this.schoolUserRepository = schoolUserRepository;
        this.categoryRepository = categoryRepository;
        this.userLikedQuestionRepository = userLikedQuestionRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
        this.notificationService = notificationService;
    }

    public boolean createNewUser(SchoolUser user) {
        boolean isSaved = false;

        if (!schoolUserRepository.existsByEmail(user.getEmail())) {
            user.setRoleName(Constants.STUDENT_ROLE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCurrentCategory("");
            user = schoolUserRepository.save(user);

            if (user.getId() > 0) {
                isSaved = true;
                addPayment(user.getId(), new Payment(0.0, "Bezpłatny dostęp do wszystkich kategorii prawa jazdy.", categoryRepository.findAll(), user));

                try {
                    emailSenderService.sendWelcomeMail(user);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }

        return isSaved;
    }

    public boolean hasUserCategory(Long userId, String nameCategory) {
        if (userId == null || nameCategory == null || nameCategory.trim().isEmpty()) {
            return false;
        }
        return schoolUserRepository.existsByIdAndAvailableCategories_NameCategory(userId, nameCategory);
    }


    public void saveUser(SchoolUser user) {
        schoolUserRepository.save(user);
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
            user.getLikedQuestions().removeIf(lq -> lq.getQuestionId().equals(questionId) && lq.getTestId().equals(testId));
            saveUser(user);
        }
    }

    public List<UserLikedQuestion> findAllLikedQuestionsByUserIdAndTestId(Long userId, Long testId) {
        return userLikedQuestionRepository.findAllBySchoolUserAndTestId(schoolUserRepository.findById(userId).orElse(null), testId);
    }

    public SchoolUser findUserByEmail(String email) {
        return schoolUserRepository.findByEmail(email);
    }

    public void changeCategory(SchoolUser user, Long categoryId) {
        Optional<SchoolUser> usr = schoolUserRepository.findById(user.getId());
        if (usr.isPresent()) {
            user = usr.get();
            Category category = categoryRepository.findById(categoryId).orElse(null);

            if (category != null && user.getAvailableCategories().contains(category)) {
                user.setCurrentCategory(category.getNameCategory());
                schoolUserRepository.save(user);
            }
        }
    }

    public void deletePayment(Long paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            SchoolUser schoolUser = payment.getSchoolUser();

            paymentRepository.delete(payment);
            List<Payment> remainingPayments = paymentRepository.findAllBySchoolUserId(schoolUser.getId());

            Set<Category> updatedCategories = remainingPayments.stream()
                    .flatMap(p -> p.getCategories().stream())
                    .collect(Collectors.toSet());

            schoolUser.setAvailableCategories(new ArrayList<>(updatedCategories));

            if (updatedCategories.stream()
                    .noneMatch(c -> c.getNameCategory().equals(schoolUser.getCurrentCategory())))
                schoolUser.setCurrentCategory("");

            schoolUserRepository.save(schoolUser);
        }
    }


    public void addPayment(Long userId, Payment payment) {
        Optional<SchoolUser> userOptional = schoolUserRepository.findById(userId);
        if (userOptional.isPresent()) {
            SchoolUser schoolUser = userOptional.get();
            List<Category> availableCategories = schoolUser.getAvailableCategories();
            List<Category> paymentCategories = payment.getCategories();

            payment.setSchoolUser(schoolUser);
            paymentRepository.save(payment);
            notificationService.sendNotificationWhenUserReceivePayment(payment);

            Set<Long> existingCategoryIds = availableCategories.stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet());
            List<Category> newCategories = paymentCategories.stream()
                    .filter(category -> !existingCategoryIds.contains(category.getId()))
                    .toList();

            if (!newCategories.isEmpty()) {
                availableCategories.addAll(newCategories);
                schoolUserRepository.save(schoolUser);
            }
        }
    }

    public void promoteUser(SchoolUser user) {
        switch (user.getRoleName()) {
            case Constants.INSTRUCTOR_ROLE:
                user.setRoleName(Constants.ADMIN_ROLE);
                schoolUserRepository.save(user);
                notificationService.sendNotificationWhenUserReceiveNewRole(user);
                break;

            case Constants.STUDENT_ROLE:
                user.setRoleName(Constants.INSTRUCTOR_ROLE);
                schoolUserRepository.save(user);
                notificationService.sendNotificationWhenUserReceiveNewRole(user);
                break;
        }
    }

    public void demoteUser(SchoolUser user) {
        switch (user.getRoleName()) {
            case Constants.ADMIN_ROLE:
                user.setRoleName(Constants.INSTRUCTOR_ROLE);
                schoolUserRepository.save(user);
                notificationService.sendNotificationWhenUserReceiveNewRole(user);
                break;

            case Constants.INSTRUCTOR_ROLE:
                user.setRoleName(Constants.STUDENT_ROLE);
                schoolUserRepository.save(user);
                notificationService.sendNotificationWhenUserReceiveNewRole(user);
                break;
        }
    }
}