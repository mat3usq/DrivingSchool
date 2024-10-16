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
import java.util.Objects;
import java.util.Optional;

@Service
public class MailService {
    private final MailRepository mailRepository;
    private final SchoolUserService schoolUserService;

    @Autowired
    public MailService(MailRepository mailRepository, SchoolUserService schoolUserService) {
        this.mailRepository = mailRepository;
        this.schoolUserService = schoolUserService;
    }

    @Transactional
    public Mail sendMail(SchoolUser sender, SchoolUser recipient, String subject, String body, Mail parentMail) {
        Mail mail = new Mail();
        mail.setSender(sender);
        mail.setRecipient(recipient);
        mail.setSubject(subject);
        mail.setBody(body);
        mail.setParentMail(parentMail);
        mail.setStatusRecipient(Constants.MAIL_UNREAD);

        if (parentMail != null)
            parentMail.getReplies().add(mail);

        return mailRepository.save(mail);
    }

    @Transactional
    public boolean sendMail(SchoolUser sender, Mail sendMail, Mail parentMail) {
        SchoolUser recipient = schoolUserService.findUserByEmail(sendMail.getRecipient().getEmail());
        if ( recipient != null && !Objects.equals(sender.getId(), recipient.getId())) {
            sendMail.setSender(sender);
            sendMail.setRecipient(recipient);
            sendMail.setParentMail(parentMail);
            sendMail.setStatusRecipient(Constants.MAIL_UNREAD);

            if (parentMail != null)
                parentMail.getReplies().add(sendMail);

            mailRepository.save(sendMail);

            return true;
        } else return false;
    }

    public List<Mail> getUnreadMailsForRecipient(SchoolUser recipient) {
        return mailRepository.findByRecipientAndStatusRecipientOrderByCreatedAtDesc(recipient, Constants.MAIL_UNREAD);
    }

    public List<Mail> getReadMailsForRecipient(SchoolUser recipient) {
        return mailRepository.findByRecipientAndStatusRecipientOrderByCreatedAtDesc(recipient, Constants.MAIL_READ);
    }

    public List<Mail> getSentMailsForSender(SchoolUser sender) {
        return mailRepository.findBySenderOrderByCreatedAtDesc(sender);
    }

    public List<Mail> getDeletedMailsForRecipient(SchoolUser recipient) {
        return mailRepository.findByRecipientAndStatusRecipientOrderByCreatedAtDesc(recipient, Constants.MAIL_DELETED);
    }

    public List<Mail> getTrashedMailsForRecipient(SchoolUser recipient) {
        return mailRepository.findByRecipientAndStatusRecipientOrderByCreatedAtDesc(recipient, Constants.MAIL_TRASHED);
    }

    public Optional<Mail> getMailById(Long mailId) {
        return mailRepository.findById(mailId);
    }

    @Transactional
    public Optional<Mail> markAsRead(Long mailId, SchoolUser recipient) {
        Optional<Mail> optionalMail = mailRepository.findById(mailId);
        if (optionalMail.isPresent()) {
            Mail mail = optionalMail.get();
            if (mail.getRecipient().equals(recipient) && Objects.equals(mail.getStatusRecipient(), Constants.MAIL_UNREAD)) {
                mail.setStatusRecipient(Constants.MAIL_READ);
                mailRepository.save(mail);
            }
            return Optional.of(mail);
        }
        return Optional.empty();
    }

    @Transactional
    public void moveToTrashRecipientMail(Long mailId, SchoolUser recipient) {
        Optional<Mail> optionalMail = mailRepository.findById(mailId);
        if (optionalMail.isPresent()) {
            Mail mail = optionalMail.get();
            if (mail.getRecipient().equals(recipient)) {
                mail.setStatusRecipient(Constants.MAIL_TRASHED);
                mailRepository.save(mail);
            }
        }
    }

    @Transactional
    public void moveRecipientMailFromTrash(Long mailId, SchoolUser recipient) {
        Optional<Mail> optionalMail = mailRepository.findById(mailId);
        if (optionalMail.isPresent()) {
            Mail mail = optionalMail.get();
            if (mail.getRecipient().equals(recipient)) {
                mail.setStatusRecipient(Constants.MAIL_READ);
                mailRepository.save(mail);
            }
        }
    }

    @Transactional
    public void deleteRecipientMail(Long mailId, SchoolUser recipient) {
        Optional<Mail> optionalMail = mailRepository.findById(mailId);
        if (optionalMail.isPresent()) {
            Mail mail = optionalMail.get();
            if (mail.getRecipient().equals(recipient)) {
                mail.setStatusRecipient(Constants.MAIL_DELETED);
                mailRepository.save(mail);
            }
        }
    }

    public List<Mail> getMailsForRecipientByStatus(SchoolUser recipient, String status) {
        return mailRepository.findByRecipientAndStatusRecipientOrderByCreatedAtDesc(recipient, status);
    }

    public List<Mail> getReadAndUnreadMailsForRecipient(SchoolUser recipient) {
        List<String> statuses = Arrays.asList(Constants.MAIL_UNREAD, Constants.MAIL_READ);
        return mailRepository.findByRecipientAndStatusRecipientInOrderByCreatedAtDesc(recipient, statuses);
    }
}
