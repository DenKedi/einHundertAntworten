package com.project.einHundertAntworten.Controller;

import com.project.einHundertAntworten.Misc.CustomUserDetailsService;
import com.project.einHundertAntworten.service.TokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final TokenService tokenService;
    private CustomUserDetailsService customUserDetailsService;

    public AuthController(TokenService tokenService, CustomUserDetailsService customUserDetailsService) {
        this.tokenService = tokenService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/token")
    public String token(Authentication authentication) {
        LOG.debug("Generating token for user: {}", authentication.getName());
        String token = tokenService.generateToken(customUserDetailsService);
        LOG.debug("Generated token: {}", token);
        return token;
    }
}
