package com.driving.school.controller;

import com.driving.school.model.SchoolUser;
import com.driving.school.service.SchoolUserService;
import com.driving.school.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    private final SchoolUserService schoolUserService;
    private final TokenService tokenService;

    @Autowired
    public LoginController(SchoolUserService schoolUserService, TokenService tokenService) {
        this.schoolUserService = schoolUserService;
        this.tokenService = tokenService;
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
    public ModelAndView catchRegisterMessage(@Valid @ModelAttribute("registerUser") SchoolUser registerUser, Errors errors,
                                             @RequestParam(required = false) String register,
                                             @RequestParam(required = false) String validation) {
        String registerErrorMessage = null;
        String registerPositiveMessage = null;
        String validationMessage = null;
        ModelAndView m = new ModelAndView();
        m.setViewName("home");

        if (register != null && register.equals("true"))
            registerPositiveMessage = "Rejestracja przebiegła pomyślnie!";
        else if (register != null && register.equals("false"))
            registerErrorMessage = "Uzytkownik pod danym emailem juz istnieje!";
        else if (validation != null && validation.equals("false"))
            validationMessage = "Wprowadz poprawne dane!";

        m.addObject("registerErrorMessage", registerErrorMessage);
        m.addObject("registerPositiveMessage", registerPositiveMessage);
        m.addObject("validationMessage", validationMessage);
        m.addObject("registerUser", registerUser);
        return m;
    }

    @PostMapping(value = "/registerUser")
    public String registerUser(@Valid @ModelAttribute("registerUser") SchoolUser user, Errors errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute("registerUser", user);
            return "redirect:/register?validation=false#login";
        }

        if (schoolUserService.createNewUser(user)) {
            redirectAttributes.addFlashAttribute("registerUser", new SchoolUser());
            return "redirect:/register?register=true#login";
        } else {
            redirectAttributes.addFlashAttribute("registerUser", user);
            return "redirect:/register?register=false#login";
        }
    }

    @GetMapping(value = "/resetPwd/infoToMail")
    public ModelAndView catchResetUsersPwdToMailInfo(@RequestParam(required = false) String resetPwd) {
        String resetPwdMessage = null;
        ModelAndView m = new ModelAndView();
        m.setViewName("home");

        if (resetPwd != null) {
            if (resetPwd.equals("true"))
                resetPwdMessage = "Instrukcje resetowania hasła zostały wysłane na Twój email.";
            else if (resetPwd.equals("false"))
                resetPwdMessage = "Nie znaleziono użytkownika z podanym adresem email.";
        }

        m.addObject("resetPwdMessage", resetPwdMessage);
        m.addObject("registerUser", new SchoolUser());
        return m;
    }

    @GetMapping(value = "/resetPwd/infoFromMail")
    public ModelAndView catchResetUsersPwdFromMailInfo(@RequestParam(required = false) String resetToken,
                                                   @RequestParam(required = false) Long userId) {
        String resetPwdTokenCorrectMessage = null;
        String resetPwdTokenWrongMessage = null;
        SchoolUser user = new SchoolUser();
        ModelAndView m = new ModelAndView();
        m.setViewName("home");

        if (resetToken != null && userId != null) {
            SchoolUser resetPwdUser = tokenService.validatePasswordResetToken(resetToken, userId);
            if (resetPwdUser != null) {
                user = resetPwdUser;
                resetPwdTokenCorrectMessage = "Proszę wprowadzić dane, aby zresetować swoje hasło.";
                m.addObject("userId", userId);
                m.addObject("resetToken", resetToken);
            } else
                resetPwdTokenWrongMessage = "Nie znaleziono użytkownika powiązanego z tym tokenem lub token wygasł.";
        } else resetPwdTokenWrongMessage = "Token resetowania hasła jest nieprawidłowy lub nie znaleziono uzytkownika.";

        m.addObject("resetPwdTokenCorrectMessage", resetPwdTokenCorrectMessage);
        m.addObject("resetPwdTokenWrongMessage", resetPwdTokenWrongMessage);
        m.addObject("registerUser", user);
        return m;
    }

    @PostMapping("/resetPwd/send")
    public String handleResetPassword(@RequestParam("resetUsersPwdEmail") String email) {
        SchoolUser user = schoolUserService.findUserByEmail(email);

        if (user != null) {
            tokenService.initiatePasswordReset(user);
            return "redirect:/resetPwd/infoToMail?resetPwd=true#login";
        } else
            return "redirect:/resetPwd/infoToMail?resetPwd=false#login";
    }
}
