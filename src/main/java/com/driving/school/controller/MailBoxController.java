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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/mailBox/trash")
    public ModelAndView displayMailsInTrash(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("mailBox");
        SchoolUser loggedInUser = (SchoolUser) session.getAttribute("loggedInUser");
        List<Mail> mails = mailService.getTrashedMailsForRecipient(loggedInUser);
        modelAndView.addObject("mails", mails);
        modelAndView.addObject("mail", new Mail());
        return modelAndView;
    }

    @PostMapping("/mailBox/sendMail")
    public String sendMail(@ModelAttribute Mail mail, HttpSession session, RedirectAttributes redirectAttributes) {
        boolean isSent = mailService.sendMail((SchoolUser) session.getAttribute("loggedInUser"), mail, null);
        redirectAttributes.addFlashAttribute("isSend", isSent);
        return "redirect:/mailBox/sent";
    }

    @PostMapping("/mailBox/moveToTrashMail")
    public String moveToTrashMail(@RequestParam("mailId") long mailId, HttpSession session) {
        mailService.moveToTrashRecipientMail(mailId, (SchoolUser) session.getAttribute("loggedInUser"));
        return "redirect:/mailBox/trash";
    }

    @PostMapping("/mailBox/moveMailFromTrash")
    public String moveMailFromTrash(@RequestParam("mailId") long mailId, HttpSession session) {
        mailService.moveRecipientMailFromTrash(mailId, (SchoolUser) session.getAttribute("loggedInUser"));
        return "redirect:/mailBox/trash";
    }

    @PostMapping("/mailBox/deleteMail")
    public String deleteMail(@RequestParam("mailId") long mailId, HttpSession session) {
        mailService.deleteRecipientMail(mailId, (SchoolUser) session.getAttribute("loggedInUser"));
        return "redirect:/mailBox/trash";
    }
}
