package com.tiagoamp.booksapi.controller;

import com.tiagoamp.booksapi.dto.AppUserRequest;
import com.tiagoamp.booksapi.dto.AppUserResponse;
import com.tiagoamp.booksapi.dto.AuthenticationRequest;
import com.tiagoamp.booksapi.dto.AuthenticationResponse;
import com.tiagoamp.booksapi.model.AppUser;
import com.tiagoamp.booksapi.service.AuthenticationService;
import com.tiagoamp.booksapi.service.UserService;
import com.tiagoamp.booksapi.util.UserMapper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final UserMapper mapper;

    @GetMapping
    public ResponseEntity<List<AppUserResponse>> getUsers() {
        List<AppUser> users = userService.find();
        var resp = users.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    @RolesAllowed("ADMIN") // needs to enable 'EnableGlobalMethodSecurity' at security class to work
    public ResponseEntity<AppUserResponse> createUser(@RequestBody @Valid AppUserRequest request) {
        var user = mapper.toModel(request);
        user = userService.save(user);
        var resp = mapper.toResponse(user);
        return ResponseEntity.created(URI.create(user.getId().toString())).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        var token = authenticationService.authenticate(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

}
