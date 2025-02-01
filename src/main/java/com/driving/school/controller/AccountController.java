package com.driving.school.controller;

import com.driving.school.model.Notification;
import com.driving.school.model.SchoolUser;
import com.driving.school.repository.NotificationRepository;
import com.driving.school.repository.SchoolUserRepository;
import com.driving.school.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class AccountController {
    private final SchoolUserRepository schoolUserRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Autowired
    public AccountController(SchoolUserRepository schoolUserRepository, NotificationService notificationService, NotificationRepository notificationRepository) {
        this.schoolUserRepository = schoolUserRepository;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/account")
    public ModelAndView displayAccount(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("account");
        SchoolUser user = schoolUserRepository.findByEmail(((SchoolUser) session.getAttribute("loggedInUser")).getEmail());
        if (user != null) {
            modelAndView.addObject("user", user);
            return modelAndView;
        }
        return new ModelAndView("redirect:/dashboard");
    }

    @GetMapping("/notifications")
    public ModelAndView displayNotifications(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("notifications");
        SchoolUser user = schoolUserRepository.findByEmail(((SchoolUser) session.getAttribute("loggedInUser")).getEmail());
        if (user != null) {
            modelAndView.addObject("notifications", notificationService.getNotificationsByUser(user));
            return modelAndView;
        }
        return new ModelAndView("redirect:/dashboard");
    }

    @PostMapping("/notifications/deleteNotification")
    public ModelAndView deleteNotification(@RequestParam("notificationId") Long notificationId, HttpSession session) {
        SchoolUser user = (SchoolUser) session.getAttribute("loggedInUser");
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (user != null && notification.isPresent())
            if (notification.get().getSchoolUser().equals(user))
                notificationRepository.deleteById(notificationId);

        return new ModelAndView("redirect:/notifications");
    }
}
