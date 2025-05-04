package com.care4elders.userservice.security;

import com.care4elders.userservice.entity.Role;
import com.care4elders.userservice.entity.User;
import com.care4elders.userservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setFirstName(oAuth2User.getAttribute("given_name"));
            user.setLastName(oAuth2User.getAttribute("family_name"));
            user.setEnabled(true);
            user.setRole(Role.NORMAL_USER);
            userRepository.save(user);
        }

        response.sendRedirect("/");  // or send a token here
    }
}
