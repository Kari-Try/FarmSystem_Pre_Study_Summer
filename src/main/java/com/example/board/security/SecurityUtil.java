package com.example.board.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 현재 로그인 사용자 ID 추출
 * SecurityContext에서 현재 로그인한 사용자의 정보를 꺼내오는 헬퍼 클래스
 * JwtProvider.getAuthentication()이 subject=userId를 principal(name)으로 넣어두었기 때문에
 * 그대로 Long으로 변환하면 된다.
 */

public class SecurityUtil {
    private SecurityUtil(){}

    // JwtProvider.getAuthentication() 이 subject=userId 를 principal/name 으로 넣으므로 그대로 사용
    public static Long currentUserIdOrThrow(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()){
            throw new IllegalStateException("Unauthenticated"); // 로그인 안 된 경우 예외
        }
        return Long.valueOf(auth.getName()); // subject=userId
    }
}
