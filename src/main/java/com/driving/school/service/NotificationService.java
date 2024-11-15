package com.driving.school.service;

import com.driving.school.model.SchoolUser;
import com.driving.school.repository.SchoolUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final SchoolUserRepository userRepository;

    @Autowired
    public NotificationService(SchoolUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void sendNotificationWhenReceivingUserRole(SchoolUser schoolUser, String role) {
    }
}
