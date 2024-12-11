package com.driving.school.repository;

import com.driving.school.model.SchoolUser;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolUserRepository extends JpaRepository<SchoolUser, Long> {
    boolean existsByEmail(String email);

    SchoolUser findByEmail(String email);

    @NonNull
    Page<SchoolUser> findAll(@NonNull Pageable pageable);

    int countAllByRoleName(String roleName);

    @Query("SELECT u FROM SchoolUser u JOIN u.availableCategories c WHERE c.nameCategory = :nameCategory")
    List<SchoolUser> findUsersByCategoryName(@Param("nameCategory") String nameCategory);

    boolean existsByIdAndAvailableCategories_NameCategory(Long userId, String nameCategory);

}
