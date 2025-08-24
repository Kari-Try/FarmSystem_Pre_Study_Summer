package com.example.board.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    private SecurityUtil(){}

    public static Long currentUserIdOrThrow(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new IllegalStateException("Unauthenticated");
        }
        UserPrincipal p = (UserPrincipal) auth.getPrincipal();
        return p.id();
    }
}
