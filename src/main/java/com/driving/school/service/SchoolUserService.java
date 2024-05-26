package com.driving.school.service;

import com.driving.school.model.Constants;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.SchoolUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class SchoolUserService {
    private final SchoolUserRepository schoolUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SchoolUserService(SchoolUserRepository schoolUserRepository, PasswordEncoder passwordEncoder) {
        this.schoolUserRepository = schoolUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean createNewUser(SchoolUser user) {
        boolean isSaved = false;

        if (!schoolUserRepository.existsByEmail(user.getEmail())) {
            user.setRoleName(Constants.STUDENT_ROLE);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user = schoolUserRepository.save(user);
            if (user.getId() > 0)
                isSaved = true;
        }

        return isSaved;
    }

    public Optional<SchoolUser> findUserById(Long id) {
        return schoolUserRepository.findById(id);
    }
}