package com.driving.school.service;

import com.driving.school.exception.ResourceNotFoundException;
import com.driving.school.model.Category;
import com.driving.school.model.SchoolUser;
import com.driving.school.model.UserStatistic;
import com.driving.school.repository.UserStatisticRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserStatisticService {

    private final UserStatisticRepository userStatisticRepository;

    @Autowired
    public UserStatisticService(UserStatisticRepository userStatisticRepository) {
        this.userStatisticRepository = userStatisticRepository;
    }


    public List<UserStatistic> getAllUserStatistics() {
        return userStatisticRepository.findAll();
    }


    public List<UserStatistic> getStatisticsByUser(SchoolUser schoolUser) {
        return userStatisticRepository.findBySchoolUser(schoolUser);
    }


    public List<UserStatistic> getStatisticsByCategory(Category category) {
        return userStatisticRepository.findByCategory(category);
    }


    public Optional<UserStatistic> getStatisticByUserAndCategory(SchoolUser schoolUser, Category category) {
        return userStatisticRepository.findBySchoolUserAndCategory(schoolUser, category);
    }


    public UserStatistic createUserStatistic(UserStatistic userStatistic) {
        return userStatisticRepository.save(userStatistic);
    }


    public UserStatistic updateUserStatistic(Long id, UserStatistic userStatisticDetails) {
        UserStatistic existingStatistic = userStatisticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserStatistic not found with id: " + id));

        existingStatistic.setDeposits(userStatisticDetails.getDeposits());
        existingStatistic.setIsExamPassed(userStatisticDetails.getIsExamPassed());
        existingStatistic.setHoursed(userStatisticDetails.getHoursed());
        existingStatistic.setHoursBuyed(userStatisticDetails.getHoursBuyed());
        existingStatistic.setSchoolUser(userStatisticDetails.getSchoolUser());
        existingStatistic.setCategory(userStatisticDetails.getCategory());

        return userStatisticRepository.save(existingStatistic);
    }


    public void deleteUserStatistic(Long id) {
        UserStatistic existingStatistic = userStatisticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserStatistic not found with id: " + id));
        userStatisticRepository.delete(existingStatistic);
    }
}
