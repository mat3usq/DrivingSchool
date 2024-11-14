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
    private final HomeController homeController;

    @Autowired
    public LoginController(SchoolUserService schoolUserService, TokenService tokenService, HomeController homeController) {
        this.schoolUserService = schoolUserService;
        this.tokenService = tokenService;
        this.homeController = homeController;
    }

    @GetMapping(value = "/login")
    public ModelAndView catchLoginMessage(@RequestParam(required = false) String error,
                                          @RequestParam(required = false) String logout,
                                          @RequestParam(required = false) String sessionExpired) {
        String loginErrorMessage = null;
        String logoutMessage = null;
        String sessionExpiredMessage = null;
        ModelAndView m = homeController.displayHomePage();

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
        ModelAndView m = homeController.displayHomePage();

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

    @GetMapping(value = "/resetPwd/infoFromMail")
    public ModelAndView catchResetUsersPwdFromMailInfo(@RequestParam(required = false) String resetToken,
                                                       @RequestParam(required = false) Long userId) {
        String resetPwdMessage = null;
        String invalidTokenMessage = null;
        SchoolUser user = new SchoolUser();
        ModelAndView m = homeController.displayHomePage();

        if (resetToken != null && userId != null) {
            SchoolUser resetPwdUser = tokenService.validatePasswordResetToken(resetToken, userId);
            if (resetPwdUser != null) {
                user = resetPwdUser;
                resetPwdMessage = "Proszę wprowadzić dane, aby zresetować swoje hasło.";
                m.addObject("userId", userId);
                m.addObject("resetToken", resetToken);
            } else
                invalidTokenMessage = "Nie znaleziono użytkownika powiązanego z tym tokenem.";
        } else invalidTokenMessage = "Niepoprawny token lub uzytkownik.";

        m.addObject("resetPwdMessage", resetPwdMessage);
        m.addObject("invalidTokenMessage", invalidTokenMessage);
        m.addObject("registerUser", user);
        return m;
    }

    @GetMapping(value = "/resetPwd/infoToMail")
    public ModelAndView catchResetUsersPwdToMailInfo(@RequestParam(required = false) String resetPwd) {
        String initiatePwdResetMessage = null;
        ModelAndView m = homeController.displayHomePage();

        if (resetPwd != null) {
            if (resetPwd.equals("true"))
                initiatePwdResetMessage = "Instrukcje resetowania hasła zostały wysłane na Twój email.";
            else if (resetPwd.equals("false"))
                initiatePwdResetMessage = "Nie znaleziono użytkownika z podanym adresem email.";
        }

        m.addObject("initiatePwdResetMessage", initiatePwdResetMessage);
        m.addObject("registerUser", new SchoolUser());
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

    @GetMapping(value = "/resetPwd")
    public ModelAndView catchResetPassword(@Valid @ModelAttribute("registerUser") SchoolUser user, Errors errors,
                                           @RequestParam(required = false) String validation,
                                           @RequestParam(required = false) String resetPwd,
                                           @RequestParam(required = false) String resetToken) {
        String resetPwdMessage = null;
        String pwdChangingMessage = null;
        ModelAndView m = homeController.displayHomePage();
        SchoolUser schoolUser = new SchoolUser();

        if (resetPwd != null && resetPwd.equals("true"))
            pwdChangingMessage = "Zmiana hasła przebiegła pomyślnie";
        else if (resetPwd != null && resetPwd.equals("false"))
            pwdChangingMessage = "Nie udało sie zresetowac hasła";
        else if (validation != null && validation.equals("false")) {
            resetPwdMessage = "Hasła nie spełniaja wymagan!";
            schoolUser = user;
            m.addObject("userId", schoolUser.getId());
            m.addObject("userToken", resetToken);
        }

        m.addObject("resetPwdMessage", resetPwdMessage);
        m.addObject("pwdChangingMessage", pwdChangingMessage);
        m.addObject("registerUser", schoolUser);
        return m;
    }

    @PostMapping("/resetPwd")
    public String resetPassword(@Valid @ModelAttribute("registerUser") SchoolUser user, Errors errors, @RequestParam(required = false) String resetToken, RedirectAttributes redirectAttributes) {
        if (tokenService.validatePasswordResetToken(resetToken, user.getId()) == null)
            return "redirect:/resetPwd/infoFromMail?resetToken=" + resetToken + "&userId=" + user.getId() + "#login";

        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute("registerUser", user);
            redirectAttributes.addFlashAttribute("resetToken", resetToken);
            return "redirect:/resetPwd?validation=false#login";
        }

        if (tokenService.resetPassword(resetToken, user.getId(), user.getPassword()))
            return "redirect:/resetPwd?resetPwd=true#login";
        else
            return "redirect:/resetPwd?resetPwd=false#login";
    }
}
