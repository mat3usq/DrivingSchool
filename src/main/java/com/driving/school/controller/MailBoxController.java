package com.driving.school.controller;

import com.driving.school.model.SchoolUser;
import com.driving.school.service.MailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

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
        return modelAndView;
    }
}
