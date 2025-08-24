package com.example.board.controller;

import com.example.board.dto.user.UserDto;
import com.example.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.board.security.SecurityUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/me")
    public UserDto me() {
        Long userId = SecurityUtil.currentUserIdOrThrow();
        return userService.me(userId);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@RequestHeader("Authorization") String auth) {
        Long userId = SecurityUtil.currentUserIdOrThrow();
        userService.softDeleteMe(userId, auth.substring(7));
        return ResponseEntity.noContent().build();
    }


}
