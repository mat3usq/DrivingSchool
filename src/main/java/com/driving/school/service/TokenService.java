package com.driving.school.service;

import com.driving.school.model.SchoolUser;
import com.driving.school.repository.SchoolUserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class TokenService {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();
    private final SchoolUserRepository userRepository;
    private final EmailSenderService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TokenService(SchoolUserRepository userRepository, EmailSenderService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateToken(int byteLength) {
        byte[] randomBytes = new byte[byteLength];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public void initiatePasswordReset(SchoolUser user) {
        String token = generateToken(32);
        String hashedToken = passwordEncoder.encode(token);
        user.setTokenForResettingPassword(hashedToken);
        user.setTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);
        try {
            emailService.sendResetPwdMail(user, token);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public SchoolUser validatePasswordResetToken(String token, Long userId) {
        Optional<SchoolUser> schoolUser = userRepository.findById(userId);
        if (schoolUser.isPresent()) {
            SchoolUser user = schoolUser.get();

            if (user.getTokenExpiry() != null && user.getTokenExpiry().isAfter(LocalDateTime.now()))
                if (user.getTokenForResettingPassword() != null)
                    if (passwordEncoder.matches(token, user.getTokenForResettingPassword()))
                        return user;
        }
        return null;
    }

    public boolean resetPassword(String token, Long userId, String newPassword) {
        SchoolUser user = validatePasswordResetToken(token, userId);
        if (user == null)
            return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTokenForResettingPassword(null);
        user.setTokenExpiry(null);
        userRepository.save(user);

        return true;
    }
}
