package com.example.board.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupReq(
        @Email @NotBlank String email,
        @Size(min = 8, max = 100) String password,
        @NotBlank String nickname
) {}
