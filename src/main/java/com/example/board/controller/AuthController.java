package com.example.board.controller;

import com.example.board.dto.auth.*;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupReq req) {
        userService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public Tokens login(@Valid @RequestBody LoginReq req) {
        return userService.login(req);
    }

    @PostMapping("/refresh")
    public Tokens refresh(@Valid @RequestBody RefreshReq req) {
        return userService.refresh(req.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String auth) {
        String token = auth.substring(7);
        userService.logout(token);
        return ResponseEntity.noContent().build();
    }
}
