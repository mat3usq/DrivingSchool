package com.driving.school.repository;

import com.driving.school.model.Role;
import com.driving.school.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByUser(User user);
}
