package com.driving.school.controller;

import com.driving.school.model.SchoolUser;
import com.driving.school.service.SchoolUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    private final SchoolUserService schoolUserService;

    @Autowired
    public LoginController(SchoolUserService schoolUserService) {
        this.schoolUserService = schoolUserService;
    }

    @GetMapping(value = "/login")
    public ModelAndView catchLoginMessage(@RequestParam(required = false) String error,
                                          @RequestParam(required = false) String logout,
                                          @RequestParam(required = false) String sessionExpired) {
        String loginErrorMessage = null;
        String logoutMessage = null;
        String sessionExpiredMessage = null;
        ModelAndView m = new ModelAndView();
        m.setViewName("home");

        if (error != null)
            loginErrorMessage = "Email lub Hasło jest niepoprawne!";

        if (logout != null)
            logoutMessage = "Wylogowałeś sie poprawnie!";

        if (sessionExpired != null)
            sessionExpiredMessage = "Twoja sesja wygasła! Zaloguj sie ponownie";


        m.addObject("loginErrorMessage", loginErrorMessage);
        m.addObject("logoutMessage", logoutMessage);
        m.addObject("sessionExpiredMessage", sessionExpiredMessage);
        m.addObject("registerUser", new SchoolUser());
        return m;
    }

    @GetMapping(value = "/register")
    public ModelAndView catchRegisterMessage(@ModelAttribute("registerUser") SchoolUser registerUser,
                                             @RequestParam(required = false) String register) {
        String registerErrorMessage = null;
        String registerPositiveMessage = null;
        ModelAndView m = new ModelAndView();
        m.setViewName("home");

        if (register != null && register.equals("true"))
            registerPositiveMessage = "Rejestracja przebiegła pomyślnie!";
        else if (register != null && register.equals("false"))
            registerErrorMessage = "Uzytkownik pod danym emailem juz istnieje!";

        m.addObject("registerErrorMessage", registerErrorMessage);
        m.addObject("registerPositiveMessage", registerPositiveMessage);
        m.addObject("registerUser", registerUser);
        return m;
    }

    @PostMapping(value = "/registerUser")
    public String registerUser(@ModelAttribute("registerUser") SchoolUser user, RedirectAttributes redirectAttributes) {
        if (schoolUserService.createNewUser(user)) {
            redirectAttributes.addFlashAttribute("registerUser", new SchoolUser());
            return "redirect:/register?register=true#login";
        } else {
            redirectAttributes.addFlashAttribute("registerUser", user);
            return "redirect:/register?register=false#login";
        }
    }
}
