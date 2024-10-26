package com.driving.school.repository;

import com.driving.school.model.Payment;
import com.driving.school.model.SchoolUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllBySchoolUser(SchoolUser schoolUser);
}
