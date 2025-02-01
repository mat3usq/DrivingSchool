package com.driving.school.repository;

import com.driving.school.model.Mail;
import com.driving.school.model.SchoolUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {
    List<Mail> findByRecipientAndStatusRecipientOrderByUpdatedAtDesc(SchoolUser recipient, String status);
    List<Mail> findBySenderAndStatusSenderOrderByUpdatedAtDesc(SchoolUser sender, String status);
    List<Mail> findByRecipientAndStatusRecipientInOrderByUpdatedAtDesc(SchoolUser recipient, List<String> statuses);
    List<Mail> findBySenderAndStatusSenderNotInOrderByUpdatedAtDesc(SchoolUser sender, List<String> statuses);
}
