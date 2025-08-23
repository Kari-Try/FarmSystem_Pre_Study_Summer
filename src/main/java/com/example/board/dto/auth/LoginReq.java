package com.example.board.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginReq(
        @Email @NotBlank String email,
        @NotBlank String password
) {}
