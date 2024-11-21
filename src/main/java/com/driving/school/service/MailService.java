package com.driving.school.service;

import com.driving.school.model.Constants;
import com.driving.school.model.Mail;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
        mail.setStatusSender(Constants.MAIL_READ);

        if (parentMail != null)
            parentMail.getReplies().add(mail);

        Mail sendMail = mailRepository.save(mail);
        updateNumberOfMails(recipient);
        return sendMail;
    }

    @Transactional
    public boolean sendMail(SchoolUser sender, Mail sendMail) {
        SchoolUser recipient = schoolUserService.findUserByEmail(sendMail.getRecipient().getEmail());
        if (recipient != null && !Objects.equals(sender, recipient)) {
            sendMail.setSender(sender);
            sendMail.setRecipient(recipient);
            sendMail.setStatusRecipient(Constants.MAIL_UNREAD);
            sendMail.setStatusSender(Constants.MAIL_READ);
            mailRepository.save(sendMail);
            updateNumberOfMails(recipient);
            return true;
        } else return false;
    }

    @Transactional
    public boolean replyOnMail(Mail replyMail, long parentMailId, SchoolUser loggedInUser) {
        Optional<Mail> optionalMail = mailRepository.findById(parentMailId);

        if (replyMail == null || replyMail.getBody() == null || replyMail.getBody().trim().isEmpty())
            return false;

        if (optionalMail.isPresent()) {
            Mail parentMail = optionalMail.get();
            if (Objects.equals(parentMail.getSender(), loggedInUser) ||
                    Objects.equals(parentMail.getRecipient(), loggedInUser)) {

                parentMail.getReplies().add(replyMail);
                replyMail.setParentMail(parentMail);
                replyMail.setSubject(parentMail.getSubject());
                boolean whoseUser = false;

                if (Objects.equals(parentMail.getSender(), loggedInUser)) {
                    parentMail.setStatusRecipient(Constants.MAIL_UNREAD);
                    replyMail.setStatusSender(Constants.MAIL_REPLY);
                    replyMail.setSender(loggedInUser);
                    replyMail.setRecipient(parentMail.getRecipient());
                    whoseUser = true;
                } else if (Objects.equals(parentMail.getRecipient(), loggedInUser)) {
                    parentMail.setStatusSender(Constants.MAIL_UNREAD);
                    replyMail.setStatusRecipient(Constants.MAIL_REPLY);
                    replyMail.setSender(loggedInUser);
                    replyMail.setRecipient(parentMail.getSender());
                }

                mailRepository.save(replyMail);
                mailRepository.save(parentMail);

                if (whoseUser)
                    updateNumberOfMails(parentMail.getRecipient());
                else updateNumberOfMails(parentMail.getSender());

                return true;
            }
        }
        return false;
    }

    @Transactional
    public Optional<Mail> markAsRead(Long mailId, SchoolUser user) {
        Optional<Mail> optionalMail = mailRepository.findById(mailId);
        if (optionalMail.isPresent()) {
            Mail mail = optionalMail.get();
            if (mail.getRecipient().equals(user) && Objects.equals(mail.getStatusRecipient(), Constants.MAIL_UNREAD)) {
                mail.setStatusRecipient(Constants.MAIL_READ);
                mailRepository.save(mail);
            } else if (mail.getSender().equals(user) && Objects.equals(mail.getStatusSender(), Constants.MAIL_UNREAD)) {
                mail.setStatusSender(Constants.MAIL_READ);
                mailRepository.save(mail);
            }
            updateNumberOfMails(user);
            return Optional.of(mail);
        }
        return Optional.empty();
    }

    @Transactional
    public void moveToTrashMail(Long mailId, SchoolUser user) {
        Optional<Mail> optionalMail = mailRepository.findById(mailId);
        if (optionalMail.isPresent()) {
            Mail mail = optionalMail.get();
            if (mail.getRecipient().equals(user)) {
                mail.setStatusRecipient(Constants.MAIL_TRASHED);
                mailRepository.save(mail);
            } else if (mail.getSender().equals(user)) {
                mail.setStatusSender(Constants.MAIL_TRASHED);
                mailRepository.save(mail);
            }
        }
    }

    @Transactional
    public void moveMailFromTrash(Long mailId, SchoolUser user) {
        Optional<Mail> optionalMail = mailRepository.findById(mailId);
        if (optionalMail.isPresent()) {
            Mail mail = optionalMail.get();
            if (mail.getRecipient().equals(user)) {
                mail.setStatusRecipient(Constants.MAIL_READ);
                mailRepository.save(mail);
            } else if (mail.getSender().equals(user)) {
                mail.setStatusSender(Constants.MAIL_READ);
                mailRepository.save(mail);
            }
        }
    }

    @Transactional
    public void deleteMail(Long mailId, SchoolUser user) {
        Optional<Mail> optionalMail = mailRepository.findById(mailId);
        if (optionalMail.isPresent()) {
            Mail mail = optionalMail.get();
            if (mail.getRecipient().equals(user)) {
                mail.setStatusRecipient(Constants.MAIL_DELETED);
                mailRepository.save(mail);
            } else if (mail.getSender().equals(user)) {
                mail.setStatusSender(Constants.MAIL_DELETED);
                mailRepository.save(mail);
            }
        }
    }

    public List<Mail> getMailsForRecipientByStatus(SchoolUser recipient, String status) {
        return mailRepository.findByRecipientAndStatusRecipientOrderByUpdatedAtDesc(recipient, status);
    }

    public List<Mail> getMailsForSenderByStatus(SchoolUser sender, String status) {
        return mailRepository.findBySenderAndStatusSenderOrderByUpdatedAtDesc(sender, status);
    }

    public List<Mail> getEmailsForUser(SchoolUser user) {
        List<Mail> mails = new ArrayList<>();
        mails.addAll(getMailsForRecipientByStatus(user, Constants.MAIL_UNREAD));
        mails.addAll(getMailsForRecipientByStatus(user, Constants.MAIL_READ));
        mails.addAll(getMailsForSenderByStatus(user, Constants.MAIL_UNREAD));
        mails.addAll(getMailsForSenderByStatus(user, Constants.MAIL_READ));
        mails.sort(Comparator.comparing(Mail::getUpdatedAt).reversed());
        return mails;
    }

    public List<Mail> getReadMailsForUser(SchoolUser user) {
        List<Mail> mails = new ArrayList<>();
        mails.addAll(getMailsForRecipientByStatus(user, Constants.MAIL_READ));
        mails.addAll(getMailsForSenderByStatus(user, Constants.MAIL_READ));
        mails.sort(Comparator.comparing(Mail::getUpdatedAt).reversed());
        return mails;
    }

    public List<Mail> getUnreadMailsForUser(SchoolUser user) {
        List<Mail> mails = new ArrayList<>();
        mails.addAll(getMailsForRecipientByStatus(user, Constants.MAIL_UNREAD));
        mails.addAll(getMailsForSenderByStatus(user, Constants.MAIL_UNREAD));
        mails.sort(Comparator.comparing(Mail::getUpdatedAt).reversed());
        return mails;
    }

    public List<Mail> getSentMailsForUser(SchoolUser user) {
        return mailRepository.findBySenderAndStatusSenderNotInOrderByUpdatedAtDesc(user, Arrays.asList(Constants.MAIL_TRASHED, Constants.MAIL_DELETED, Constants.MAIL_REPLY));
    }

    public List<Mail> getTrashedMailsForUser(SchoolUser user) {
        List<Mail> mails = new ArrayList<>();
        mails.addAll(getMailsForRecipientByStatus(user, Constants.MAIL_TRASHED));
        mails.addAll(getMailsForSenderByStatus(user, Constants.MAIL_TRASHED));
        mails.sort(Comparator.comparing(Mail::getUpdatedAt).reversed());
        return mails;
    }

    private void updateNumberOfMails(SchoolUser user) {
        user.setNumberOfMails(getUnreadMailsForUser(user).size());
        schoolUserService.saveUser(user);
    }
}
