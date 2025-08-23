package com.example.board.dto.user;

import com.example.board.domain.user.User;

public record UserDto(Long id, String email, String nickname) {
    public static UserDto from(User u) { return new UserDto(u.getId(), u.getEmail(), u.getNickname()); }
}
