package com.example.board.controller;

import com.example.board.dto.user.UserDto;
import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal(expression = "principal") Long userId) {
        return userService.me(userId);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal(expression = "principal") Long userId,
                                         @RequestHeader("Authorization") String auth) {
        userService.softDeleteMe(userId, auth.substring(7));
        return ResponseEntity.noContent().build();
    }
}
