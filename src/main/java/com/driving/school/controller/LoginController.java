package com.driving.school.controller;

import com.driving.school.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView displayLoginPage(@RequestParam(required = false) String error,
                                         @RequestParam(required = false) String logout,
                                         @RequestParam(required = false) String register) {
        String loginMessage = null;
        String registerMessage;
        String logoutMessage = null;
        ModelAndView m = new ModelAndView();
        m.setViewName("home");

        if (error != null)
            loginMessage = "Login lub Password jest niepoprawne!";

        if (register != null && register.equals("true"))
            registerMessage = "Rejestracja przebiegła pomyślnie! Zaloguj sie swoimi danymi.";
        else
            registerMessage = "Rejestracja przebiegła negatywnie. Błąd Zapisu danych.";

        if (logout != null)
            logoutMessage = "Wylogowałeś sie poprawnie!";

        m.addObject("loginMessage", loginMessage);
        m.addObject("registerMessage", registerMessage);
        m.addObject("logoutMessage", logoutMessage);
        m.addObject("registerUser", new User());
        m.addObject("loginUser", new User());
        return m;
    }
}
