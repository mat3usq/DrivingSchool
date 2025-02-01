package com.driving.school.service;

import com.driving.school.model.SchoolUser;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailSenderService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailSenderService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendWelcomeMail(SchoolUser user) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("drivingschool.servicesystem@gmail.com");
        helper.setTo(user.getEmail());
        helper.setSubject("Witamy w Driving School!");

        Context context = new Context();
        context.setVariable("user", user);

        String htmlContent = templateEngine.process("welcome-email.html", context);
        helper.setText(htmlContent, true);

        ClassPathResource image = new ClassPathResource("static/assets/img/logo.png");
        helper.addInline("logoImage", image);

        mailSender.send(message);
    }

    @Async
    public void sendResetPwdMail(SchoolUser user, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("drivingschool.servicesystem@gmail.com");
        helper.setTo(user.getEmail());
        helper.setSubject("Resetowanie Hasła - Driving School");

        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("token", token);

        String htmlContent = templateEngine.process("resetPwd-email.html", context);
        helper.setText(htmlContent, true);

        ClassPathResource image = new ClassPathResource("static/assets/img/logo.png");
        helper.addInline("logoImage", image);

        mailSender.send(message);
    }
}
