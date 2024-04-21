package com.driving.school.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/createUser"))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/dashboard/**").authenticated()
                        .requestMatchers("/", "/home").permitAll()
                        .requestMatchers("/registerUser").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/assets/**").permitAll());
//                .formLogin(loginConfigurer -> loginConfigurer.loginPage("/login")
//                        .defaultSuccessUrl("/dashboard").failureUrl("/").permitAll())
//                .logout(logoutConfigurer -> logoutConfigurer.logoutSuccessUrl("/login?logout=true")
//                        .invalidateHttpSession(true).permitAll());
//                .httpBasic(Customizer.withDefaults());

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
