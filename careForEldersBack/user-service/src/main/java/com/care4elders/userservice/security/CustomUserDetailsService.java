package com.care4elders.userservice.security;

import com.care4elders.userservice.repository.UserRepository;
import com.care4elders.userservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        System.out.println("🔍 Loaded user: " + user.getEmail());
        System.out.println("🔐 Hashed password: " + user.getPassword());

        // For debug only (remove in prod):
        boolean matches = new BCryptPasswordEncoder().matches("secret123", user.getPassword());
        System.out.println("✅ Does 'secret123' match hash? → " + matches);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

}
