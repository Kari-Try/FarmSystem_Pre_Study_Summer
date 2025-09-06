package com.example.board.controller;


import com.example.board.dto.post.PostDetailRes;
import com.example.board.dto.post.PostListRes;
import com.example.board.dto.user.NicknameUpdateReq;
import com.example.board.dto.user.UserDto;
import com.example.board.security.SecurityUtil;
import com.example.board.service.MyPageService;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final UserService userService;
    private final MyPageService myPageService;

    //유저 정보 조회
    @GetMapping("/me")
    public UserDto me() {
        Long userId = SecurityUtil.currentUserIdOrThrow();

        return userService.me(userId);
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String auth) {
        myPageService.logout(auth.substring(7));
        return ResponseEntity.noContent().build();
    }

    //회원탈퇴(soft delete)
    @PostMapping("/me")
    public ResponseEntity<Void> deleteMe(@RequestHeader("Authorization") String auth) {
        Long userId = SecurityUtil.currentUserIdOrThrow();
        myPageService.softDeleteMe(userId, auth.substring(7));
        return ResponseEntity.noContent().build();
    }


    //닉네임 변경
    @PatchMapping("/me/nickname")
    public ResponseEntity<Void> changeNickname(@Valid @RequestBody NicknameUpdateReq req) {
        Long userId = SecurityUtil.currentUserIdOrThrow();
        userService.changeNickname(userId, req.getNickname());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

   //내가 쓴 게시글 모아보기
    @GetMapping("/posts")
    public ResponseEntity<Page<PostListRes>> myPosts(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(myPageService.myPosts(pageable));
    }

    //내가 쓴 게시글 상세조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDetailRes> myPostDetail(@PathVariable Long postId) {
        return ResponseEntity.ok(myPageService.myPostDetail(postId));
    }

    //내가 좋아요 한 게시글 조회
    @GetMapping("/likes")
    public ResponseEntity<Page<PostListRes>> myLikedPosts(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(myPageService.myLikedPosts(pageable));
    }

    //내가 좋아요한 게시글 상세 조회=
    @GetMapping("/likes/{postId}")
    public ResponseEntity<PostDetailRes> myLikedPostDetail(@PathVariable Long postId) {
        return ResponseEntity.ok(myPageService.myLikedPostDetail(postId));
    }


}
