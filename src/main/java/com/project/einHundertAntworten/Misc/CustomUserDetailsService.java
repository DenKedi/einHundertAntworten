package com.project.einHundertAntworten.Misc;

import com.project.einHundertAntworten.User.User;
import com.project.einHundertAntworten.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;
    private Authentication lastAuthentication;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
       if (username.contains("@")){
           username = username.substring(0, username.length() - 1);
           user = userRepository.findByEmail(username);
        }else{
           user = userRepository.findByUsername(username);
       }
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER", "ADMIN") // Add roles or authorities as needed
                .build();
        // Manually create Authentication object
        Authentication authentication = createAuthentication(user.getUsername(), user.getPassword(), user.getRole());
        // Set the Authentication object in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        lastAuthentication = authentication;


        return userDetails;
    }
    public static Authentication createAuthentication(String username, String password, String role) {
        // Create a list of authorities (roles)
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        // In a real-world scenario, you might have more authorities based on user roles/permissions

        // Create the Authentication object with the principal (username), credentials (password), and authorities
        return new UsernamePasswordAuthenticationToken(username, password, Collections.singletonList(authority));
    }

    public Authentication getLastAuthentication() {

        return lastAuthentication;
    }


}
