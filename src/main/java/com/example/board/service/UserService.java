// service/UserService.java
package com.example.board.service;

import com.example.board.domain.user.User;
import com.example.board.dto.auth.*;
import com.example.board.dto.user.UserDto;
import com.example.board.exception.ConflictException;
import com.example.board.exception.UnauthorizedException;
import com.example.board.repository.user.UserRepository;
import com.example.board.security.JwtProvider;
import com.example.board.token.TokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtProvider jwt;
    private final TokenStore tokenStore;

    @Transactional
    public void signup(SignupReq req) {
        if (userRepo.existsByEmailAndDeletedFalse(req.email()))
            throw new ConflictException("Email already in use");
        var user = new User();
        user.setEmail(req.email());
        user.setPassword(encoder.encode(req.password()));
        user.setNickname(req.nickname());
        userRepo.save(user);
    }

    @Transactional
    public Tokens login(LoginReq req) {
        var user = userRepo.findByEmailAndDeletedFalse(req.email())
                .orElseThrow(UnauthorizedException::new);
        if (!encoder.matches(req.password(), user.getPassword()))
            throw new UnauthorizedException();

        var access = jwt.generateAccessToken(user);
        var refresh = jwt.generateRefreshToken(user);
        tokenStore.saveRefresh(user.getId(), refresh, 60L * 60 * 24 * 14);
        return new Tokens(access, refresh);
    }

    @Transactional(readOnly = true)
    public UserDto me(Long userId) {
        var user = userRepo.findById(userId).orElseThrow(UnauthorizedException::new);
        return UserDto.from(user);
    }

    @Transactional
    public Tokens refresh(String refreshToken) {
        var claims = jwt.parse(refreshToken).getBody();
        Long uid = Long.valueOf(claims.getSubject());
        if (!tokenStore.matchRefresh(uid, refreshToken)) throw new UnauthorizedException();
        var user = userRepo.getReferenceById(uid);
        var access = jwt.generateAccessToken(user);
        // 초기 MVP는 Refresh 재사용(로테이션은 추후)
        return new Tokens(access, refreshToken);
    }

    @Transactional
    public void logout(String accessToken) {
        var claims = jwt.parse(accessToken).getBody();
        Long uid = Long.valueOf(claims.getSubject());
        tokenStore.revokeRefresh(uid);
        tokenStore.blacklistAccess(accessToken);
    }

    @Transactional
    public void softDeleteMe(Long userId, String accessToken) {
        var user = userRepo.findById(userId).orElseThrow(UnauthorizedException::new);
        user.setDeleted(true);
        user.setDeletedAt(java.time.LocalDateTime.now());
        tokenStore.revokeRefresh(userId);
        tokenStore.blacklistAccess(accessToken);
    }

    //닉네임 변경
    @Transactional
    public void changeNickname(Long userId, String nickname) {
       var u = userRepo.findById(userId).orElseThrow(UnauthorizedException::new);

       u.setNickname(nickname);

    }

}
