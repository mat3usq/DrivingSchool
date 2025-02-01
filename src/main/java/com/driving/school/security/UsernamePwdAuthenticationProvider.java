package com.driving.school.security;

import com.driving.school.model.SchoolUser;
import com.driving.school.repository.SchoolUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UsernamePwdAuthenticationProvider implements AuthenticationProvider {
    private final SchoolUserRepository schoolUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsernamePwdAuthenticationProvider(SchoolUserRepository schoolUserRepository, PasswordEncoder passwordEncoder) {
        this.schoolUserRepository = schoolUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        SchoolUser user = schoolUserRepository.findByEmail(email);
        if (user != null && user.getId() > 0 && passwordEncoder.matches(pwd, user.getPassword()))
            return new UsernamePasswordAuthenticationToken(email, user.getPassword(), getGrantedAuthorities(user));
        else
            throw new BadCredentialsException("Invalid credentials!");
    }

    private List<GrantedAuthority> getGrantedAuthorities(SchoolUser user) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRoleName()));
        return grantedAuthorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
