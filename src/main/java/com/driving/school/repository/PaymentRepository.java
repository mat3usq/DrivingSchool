package com.driving.school.repository;

import com.driving.school.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllBySchoolUserId(Long schoolUserId);
    @Query("SELECT SUM(p.sum) FROM Payment p WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate")
    Double sumPaymentsBetween(LocalDateTime startDate, LocalDateTime endDate);
}
