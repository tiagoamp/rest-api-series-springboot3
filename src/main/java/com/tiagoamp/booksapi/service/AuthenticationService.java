package com.tiagoamp.booksapi.service;

import com.tiagoamp.booksapi.exception.AuthenticationFailedException;
import com.tiagoamp.booksapi.model.AppUser;
import com.tiagoamp.booksapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public String authenticate(String email, String password) {
        try {
            // The authentication manager provides secure authentication and throws exception if it fails
            var authToken = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authenticate = authenticationManager.authenticate(authToken);
            var user  = (AppUser) authenticate.getPrincipal();
            String token = tokenService.generateToken(user);
            return token;
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Invalid User or Password");
        }
    }

}
