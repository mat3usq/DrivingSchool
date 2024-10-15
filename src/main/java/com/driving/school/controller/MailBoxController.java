package com.driving.school.controller;

import com.driving.school.model.Mail;
import com.driving.school.model.SchoolUser;
import com.driving.school.service.MailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class MailBoxController {
    private final MailService mailService;

    @Autowired
    public MailBoxController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/mailBox")
    public ModelAndView displayMails(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("mailBox");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        modelAndView.addObject("mails", mailService.getReadAndUnreadMailsForRecipient(loggedInUser));
        modelAndView.addObject("mail", new Mail());
        return modelAndView;
    }

    @GetMapping("/mailBox/read")
    public ModelAndView displayReadMails(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("mailBox");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        modelAndView.addObject("mails", mailService.getReadMailsForRecipient(loggedInUser));
        modelAndView.addObject("mail", new Mail());
        return modelAndView;
    }

    @GetMapping("/mailBox/unread")
    public ModelAndView displayUnreadMails(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("mailBox");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        modelAndView.addObject("mails", mailService.getUnreadMailsForRecipient(loggedInUser));
        modelAndView.addObject("mail", new Mail());
        return modelAndView;
    }

    @GetMapping("/mailBox/sent")
    public ModelAndView displaySentMails(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("mailBox");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        List<Mail> mails = mailService.getSentMailsForSender(loggedInUser);
        modelAndView.addObject("mails", mails);
        modelAndView.addObject("mail", new Mail());
        return modelAndView;
    }

    @PostMapping("/mailBox/sendMail")
    public ModelAndView sendMail(@ModelAttribute Mail mail, HttpSession session) {
        ModelAndView modelAndView = displayMails(session);
        if (mailService.sendMail((SchoolUser) session.getAttribute("loggedInUser"), mail, null))
            modelAndView.addObject("isSend", true);
        else
            modelAndView.addObject("isSend", false);
        return modelAndView;
    }
}
