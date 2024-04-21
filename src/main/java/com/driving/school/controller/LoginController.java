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
        String loginErrorMessage = null;
        String registerErrorMessage = null;
        String registerPositiveMessage = null;
        String logoutMessage = null;
        ModelAndView m = new ModelAndView();
        m.setViewName("home");

        if (error != null)
            loginErrorMessage = "Login lub Password jest niepoprawne!";

        if (register != null && register.equals("true"))
            registerPositiveMessage = "Rejestracja przebiegła pomyślnie! Zaloguj sie swoimi danymi.";
        else
            registerErrorMessage = "Uzytkownik pod danym emailem juz istnieje!";

        if (logout != null)
            logoutMessage = "Wylogowałeś sie poprawnie!";

        m.addObject("loginErrorMessage", loginErrorMessage);
        m.addObject("registerErrorMessage", registerErrorMessage);
        m.addObject("registerPositiveMessage", registerPositiveMessage);
        m.addObject("logoutMessage", logoutMessage);
        m.addObject("registerUser", new User());
        m.addObject("loginUser", new User());
        return m;
    }
}
