package com.driving.school.service;

import com.driving.school.model.Constants;
import com.driving.school.model.Role;
import com.driving.school.model.User;
import com.driving.school.repository.RoleRepository;
import com.driving.school.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean createNewUser(User user) {
        boolean isSaved = false;

        if(!userRepository.existsByEmail(user.getEmail()))
        {
            Set<Role> roles = new LinkedHashSet<>();
            Role role = new Role(Constants.STUDENT_ROLE, user);
            roles.add(role);

            user.setRoles(roles);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user = userRepository.save(user);
            role = roleRepository.save(role);
            if (user.getId() > 0 && user.getRoles() != null && role.getId() > 0)
                isSaved = true;
        }

        return isSaved;
    }
}
