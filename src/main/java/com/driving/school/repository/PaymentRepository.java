package com.driving.school.repository;

import com.driving.school.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllBySchoolUserId(Long schoolUserId);
}
