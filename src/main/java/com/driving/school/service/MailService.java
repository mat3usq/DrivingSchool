package com.driving.school.service;

import com.driving.school.model.Constants;
import com.driving.school.model.Mail;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class MailService {
    private final MailRepository mailRepository;

    @Autowired
    public MailService(MailRepository mailRepository) {
        this.mailRepository = mailRepository;
    }

    @Transactional
    public Mail sendMail(SchoolUser sender, SchoolUser recipient, String subject, String body, Mail parentMail) {
        Mail mail = new Mail();
        mail.setSender(sender);
        mail.setRecipient(recipient);
        mail.setSubject(subject);
        mail.setBody(body);
        mail.setParentMail(parentMail);
        mail.setStatusSender(Constants.MAIL_SENT);
        mail.setStatusRecipient(Constants.MAIL_UNREAD);

        if (parentMail != null)
            parentMail.getReplies().add(mail);

        return mailRepository.save(mail);
    }

    public List<Mail> getUnreadMailsForRecipient(SchoolUser recipient) {
        return mailRepository.findByRecipientAndStatusRecipient(recipient, Constants.MAIL_UNREAD);
    }

    public List<Mail> getReadMailsForRecipient(SchoolUser recipient) {
        return mailRepository.findByRecipientAndStatusRecipient(recipient, Constants.MAIL_READ);
    }

    public List<Mail> getSentMailsForSender(SchoolUser sender) {
        return mailRepository.findBySender(sender);
    }

    public List<Mail> getDeletedMailsForRecipient(SchoolUser recipient) {
        return mailRepository.findByRecipientAndStatusRecipient(recipient, Constants.MAIL_DELETED);
    }

    public List<Mail> getDeletedMailsForSender(SchoolUser sender) {
        return mailRepository.findBySenderAndStatusSender(sender, Constants.MAIL_DELETED);
    }

    public Optional<Mail> getMailById(Long mailId) {
        return mailRepository.findById(mailId);
    }

    @Transactional
    public Optional<Mail> markAsRead(Long mailId, SchoolUser recipient) {
        Optional<Mail> optionalMail = mailRepository.findById(mailId);
        if (optionalMail.isPresent()) {
            Mail mail = optionalMail.get();
            if (mail.getRecipient().equals(recipient) && mail.getStatusRecipient() == Constants.MAIL_UNREAD) {
                mail.setStatusRecipient(Constants.MAIL_READ);
                mailRepository.save(mail);
            }
            return Optional.of(mail);
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Mail> deleteMailForRecipient(Long mailId, SchoolUser recipient) {
        Optional<Mail> optionalMail = mailRepository.findById(mailId);
        if (optionalMail.isPresent()) {
            Mail mail = optionalMail.get();
            if (mail.getRecipient().equals(recipient)) {
                mail.setStatusRecipient(Constants.MAIL_DELETED);
                mailRepository.save(mail);
            }
            return Optional.of(mail);
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Mail> deleteMailForSender(Long mailId, SchoolUser sender) {
        Optional<Mail> optionalMail = mailRepository.findById(mailId);
        if (optionalMail.isPresent()) {
            Mail mail = optionalMail.get();
            if (mail.getSender().equals(sender)) {
                mail.setStatusSender(Constants.MAIL_DELETED);
                mailRepository.save(mail);
            }
            return Optional.of(mail);
        }
        return Optional.empty();
    }

    public List<Mail> getMailsForRecipientByStatus(SchoolUser recipient, String status) {
        return mailRepository.findByRecipientAndStatusRecipient(recipient, status);
    }

    public List<Mail> getMailsForSenderByStatus(SchoolUser sender, String status) {
        return mailRepository.findBySenderAndStatusSender(sender, status);
    }

    public List<Mail> getReadAndUnreadMailsForRecipient(SchoolUser recipient) {
        List<String> statuses = Arrays.asList(Constants.MAIL_UNREAD, Constants.MAIL_READ);
        return mailRepository.findByRecipientAndStatusRecipientInOrderByCreatedAtAsc(recipient, statuses);
    }
}
