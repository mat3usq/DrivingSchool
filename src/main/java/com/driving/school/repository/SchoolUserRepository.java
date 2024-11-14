package com.driving.school.repository;

import com.driving.school.model.SchoolUser;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolUserRepository extends JpaRepository<SchoolUser, Long> {
    boolean existsByEmail(String email);
    SchoolUser findByEmail(String email);
    @NonNull
    Page<SchoolUser> findAll(@NonNull Pageable pageable);
    int countAllByRoleName(String roleName);
}
