package com.driving.school.controller;

import com.driving.school.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
    @GetMapping(value = "/login")
    public ModelAndView catchLoginMessage(@RequestParam(required = false) String error,
                                         @RequestParam(required = false) String logout) {
        String loginErrorMessage = null;
        String logoutMessage = null;
        ModelAndView m = new ModelAndView();
        m.setViewName("home");

        if (error != null)
            loginErrorMessage = "Email lub Hasło jest niepoprawne!";

        if (logout != null)
            logoutMessage = "Wylogowałeś sie poprawnie!";

        m.addObject("loginErrorMessage", loginErrorMessage);
        m.addObject("logoutMessage", logoutMessage);
        m.addObject("registerUser", new User());
        return m;
    }

    @GetMapping(value = "/register")
    public ModelAndView catchRegisterMessage(@RequestParam(required = false) String register) {
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
        m.addObject("registerUser", new User());
        return m;
    }
}
