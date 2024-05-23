package com.driving.school.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.io.IOException;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.csrfTokenRepository(new CookieCsrfTokenRepository()))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/dashboard/**").authenticated()
                        .requestMatchers("/lecture").authenticated()
                        .requestMatchers("/lecture/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers("/calendar/**").authenticated()
                        .requestMatchers("/callendar-next").authenticated()
                        .requestMatchers("/loginUser").permitAll()
                        .requestMatchers("/registerUser").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/logout").authenticated()
                        .requestMatchers("/assets/**").permitAll())
                .formLogin(loginConfigurer -> loginConfigurer
                        .loginProcessingUrl("/loginUser")
                        .loginPage("/home")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true").permitAll())
                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll())
                .exceptionHandling(handler -> {
                    handler.accessDeniedHandler(new CustomAccessDeniedHandler());
                })
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        int cpuCost = 65536; // 2^16
        int memoryCost = 8;
        int parallelization = 1;
        int keyLength = 32;
        int saltLength = 64;
        return new SCryptPasswordEncoder(cpuCost, memoryCost, parallelization, keyLength, saltLength);
    }

    private static class CustomAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated())
                if (request.getRequestURI().startsWith("/lecture"))
                    response.sendRedirect("/lecture");
                else
                    response.sendRedirect("/dashboard");
            else
                response.sendRedirect("/home");
        }
    }
}
