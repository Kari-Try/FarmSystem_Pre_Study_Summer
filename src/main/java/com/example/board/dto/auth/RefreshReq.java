package com.example.board.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshReq(@NotBlank String refreshToken) {}
