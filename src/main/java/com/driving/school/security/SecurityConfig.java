package com.driving.school.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.csrfTokenRepository(new CookieCsrfTokenRepository())
                        .ignoringRequestMatchers("/registerUser", "/loginUser"))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/dashboard/**").authenticated()
                        .requestMatchers("/lecture/**").authenticated()
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
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll())
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
}
