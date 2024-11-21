package com.driving.school.controller;

import com.driving.school.model.Constants;
import com.driving.school.model.Mail;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.service.MailService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class MailBoxController {
    private final MailService mailService;
    private final SchoolUserRepository schoolUserRepository;

    @Autowired
    public MailBoxController(MailService mailService,
                             SchoolUserRepository schoolUserRepository) {
        this.mailService = mailService;
        this.schoolUserRepository = schoolUserRepository;
    }

    @GetMapping("/mailBox")
    public ModelAndView displayMails(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("mailBox");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        modelAndView.addObject("mails", mailService.getEmailsForUser(loggedInUser));
        modelAndView.addObject("mail", new Mail());
        return modelAndView;
    }

    @GetMapping("/mailBox/read")
    public ModelAndView displayReadMails(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("mailBox");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        modelAndView.addObject("mails", mailService.getReadMailsForUser(loggedInUser));
        modelAndView.addObject("mail", new Mail());
        return modelAndView;
    }

    @GetMapping("/mailBox/unread")
    public ModelAndView displayUnreadMails(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("mailBox");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        modelAndView.addObject("mails", mailService.getUnreadMailsForUser(loggedInUser));
        modelAndView.addObject("mail", new Mail());
        return modelAndView;
    }

    @GetMapping("/mailBox/sent")
    public ModelAndView displaySentMails(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("mailBox");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        List<Mail> mails = mailService.getSentMailsForUser(loggedInUser);
        modelAndView.addObject("mails", mails);
        modelAndView.addObject("mail", new Mail());
        return modelAndView;
    }

    @PostMapping("/mailBox/sendMail")
    public ModelAndView sendMail(@Valid @ModelAttribute Mail mail, Errors errors, HttpSession session, RedirectAttributes redirectAttributes) {
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        SchoolUser recipent = schoolUserRepository.findByEmail(mail.getRecipient().getEmail());

        if ((errors.hasErrors() || recipent == null || recipent.equals(loggedInUser)) && !loggedInUser.getRoleName().equals(Constants.ADMIN_ROLE)) {
            ModelAndView modelAndView = new ModelAndView("mailBox");
            List<Mail> mails = mailService.getSentMailsForUser(loggedInUser);
            modelAndView.addObject("mails", mails);
            modelAndView.addObject("mail", mail);
            modelAndView.addObject("createMailValidationInfo", "Wprowadź poprawne dane, aby wysłac wiadomosc!");
            if (recipent == null)
                modelAndView.addObject("adressMailValidationInfo", "Nie znaleziono uzytkownika o podanym adresie e-mail!");
            if (recipent != null && recipent.equals(loggedInUser))
                modelAndView.addObject("adressMailValidationInfo", "Nie mozesz byc odbiorca wiadomosci!");

            return modelAndView;
        }

        boolean isSent = mailService.sendMail(loggedInUser, mail);
        redirectAttributes.addFlashAttribute("isSend", isSent);
        return new ModelAndView("redirect:/mailBox/sent");
    }

    @GetMapping("/mailBox/trash")
    public ModelAndView displayMailsInTrash(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("mailBox");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        List<Mail> mails = mailService.getTrashedMailsForUser(loggedInUser);
        modelAndView.addObject("mails", mails);
        modelAndView.addObject("mail", new Mail());
        return modelAndView;
    }

    @PostMapping("/mailBox/moveToTrashMail")
    public String moveToTrashMail(@RequestParam("mailId") long mailId, HttpSession session) {
        mailService.moveToTrashMail(mailId, (SchoolUser) session.getAttribute("loggedInUser"));
        return "redirect:/mailBox/trash";
    }

    @PostMapping("/mailBox/moveMailFromTrash")
    public String moveMailFromTrash(@RequestParam("mailId") long mailId, HttpSession session) {
        mailService.moveMailFromTrash(mailId, (SchoolUser) session.getAttribute("loggedInUser"));
        return "redirect:/mailBox/trash";
    }

    @PostMapping("/mailBox/deleteMail")
    public String deleteMail(@RequestParam("mailId") long mailId, HttpSession session) {
        mailService.deleteMail(mailId, (SchoolUser) session.getAttribute("loggedInUser"));
        return "redirect:/mailBox/trash";
    }

    @PostMapping("/mailBox/showMail")
    public ModelAndView showMail(@RequestParam("mailId") long mailId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("showMail");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<Mail> optionalMail = mailService.markAsRead(mailId, loggedInUser);
        if (optionalMail.isPresent() && (
                Objects.equals(optionalMail.get().getRecipient().getId(), loggedInUser.getId())
                        || Objects.equals(optionalMail.get().getSender().getId(), loggedInUser.getId())
        )) {
            modelAndView.addObject("showedMail", optionalMail.get());
            modelAndView.addObject("mail", new Mail());
        } else return displayMails(session);
        return modelAndView;
    }

    @PostMapping("/mailBox/getMailToReply")
    public ModelAndView getMailToReply(@RequestParam("mailId") long mailId, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("replyOnMail");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<Mail> optionalMail = mailService.markAsRead(mailId, loggedInUser);
        if (optionalMail.isPresent() && (
                Objects.equals(optionalMail.get().getRecipient().getId(), loggedInUser.getId())
                        || Objects.equals(optionalMail.get().getSender().getId(), loggedInUser.getId())
        )) {
            modelAndView.addObject("showedMail", optionalMail.get());
            modelAndView.addObject("mail", new Mail());
        } else return displayMails(session);
        return modelAndView;
    }

    @PostMapping("/mailBox/replyOnMail")
    public String replyOnMail(@ModelAttribute Mail replyMail, @RequestParam("parentMailId") long parentMailId, HttpSession session, RedirectAttributes redirectAttributes) {
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        boolean isSend = mailService.replyOnMail(replyMail, parentMailId, loggedInUser);
        redirectAttributes.addFlashAttribute("isSend", isSend);
        return "redirect:/mailBox";
    }
}
