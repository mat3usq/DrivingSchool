package com.driving.school.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SimpleRedirectInvalidSessionStrategy;
import org.springframework.security.web.session.SimpleRedirectSessionInformationExpiredStrategy;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class SecurityConfig {
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomUserDetailsService userDetailsService;
    private final DataSource dataSource;

    @Autowired
    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler, CustomUserDetailsService userDetailsService, DataSource dataSource) {
        this.successHandler = successHandler;
        this.userDetailsService = userDetailsService;
        this.dataSource = dataSource;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.csrfTokenRepository(new CookieCsrfTokenRepository()))
                .authorizeHttpRequests(requests -> requests
                        // dashboard
                        .requestMatchers("/dashboard", "/dashboard/changeCurrentCategory").authenticated()
                        .requestMatchers("/dashboard/student/**").hasRole("STUDENT")
                        .requestMatchers("/dashboard/instructor/**").hasRole("INSTRUCTOR")
                        .requestMatchers("/dashboard/admin/**").hasRole("ADMIN")
                        // course
                        .requestMatchers("/course/showCourse").authenticated()
                        .requestMatchers("/course/instructor/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers("/course/admin/**").hasRole("ADMIN")
                        // lecture
                        .requestMatchers("/lecture").authenticated()
                        .requestMatchers("/lecture/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        // calendar
                        .requestMatchers("/calendar").authenticated()
                        .requestMatchers("/calendar/operation/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        .requestMatchers("/calendar/student/**").hasRole("STUDENT")
                        // mailBox
                        .requestMatchers("/mailBox/**").authenticated()
                        // account
                        .requestMatchers("/account").authenticated()
                        // account
                        .requestMatchers("/notifications/**").authenticated()
                        // tests
                        .requestMatchers("/tests/**").authenticated()
                        // exam
                        .requestMatchers("/exam/**").authenticated()
                        // log / reg
                        .requestMatchers("/loginUser").permitAll()
                        .requestMatchers("/registerUser").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/resetPwd/**").permitAll()
                        .requestMatchers("/logout").authenticated()
                        .requestMatchers("/assets/**").permitAll()
                        .requestMatchers("/error").authenticated())
                .formLogin(loginConfigurer -> loginConfigurer
                        .loginProcessingUrl("/loginUser")
                        .loginPage("/home")
                        .successHandler(successHandler)
                        .failureUrl("/login?error=true").permitAll())
                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll())
                .rememberMe(rememberMe -> rememberMe
                        .userDetailsService(userDetailsService)
                        .tokenRepository(persistentTokenRepository())
                        .tokenValiditySeconds(60 * 60 * 24 * 7) // 7 dni
                        .key("B{sF6,bAxD!@1Oe@PAGnhV-jfZWNq4QEwYip,NW.N29cn=ID$4")
                        .authenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/dashboard"))
                        .rememberMeParameter("rememberMe"))

                .sessionManagement(session -> session
                        .invalidSessionStrategy(new SimpleRedirectInvalidSessionStrategy("/login?sessionExpired=true"))
                        .maximumSessions(1)
                        .expiredSessionStrategy(new SimpleRedirectSessionInformationExpiredStrategy("/login?sessionExpired=true")))

                .exceptionHandling(handler -> handler.accessDeniedHandler(new CustomAccessDeniedHandler()))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);

        // drop table with start app
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS persistent_logins");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tokenRepository.setCreateTableOnStartup(true);

        return tokenRepository;
    }

    private static class CustomAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated())
                if (request.getRequestURI().startsWith("/course"))
                    response.sendRedirect("/course");
                else if (request.getRequestURI().startsWith("/lecture"))
                    response.sendRedirect("/lecture");
                else if (request.getRequestURI().startsWith("/calendar"))
                    response.sendRedirect("/calendar");
                else if (request.getRequestURI().startsWith("/mailBox"))
                    response.sendRedirect("/mailBox");
                else if (request.getRequestURI().startsWith("/account"))
                    response.sendRedirect("/account");
                else if (request.getRequestURI().startsWith("/notifications"))
                    response.sendRedirect("/notifications");
                else if (request.getRequestURI().startsWith("/tests"))
                    response.sendRedirect("/tests");
                else if (request.getRequestURI().startsWith("/exam"))
                    response.sendRedirect("/exam");
                else
                    response.sendRedirect("/dashboard");
            else
                response.sendRedirect("/home");
        }
    }
}
