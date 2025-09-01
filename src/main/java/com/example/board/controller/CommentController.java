package com.example.board.controller;


import com.example.board.dto.comment.CommentReq;
import com.example.board.dto.comment.CommentRes;
import com.example.board.security.UserPrincipal;
import com.example.board.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    //댓글 생성
    @PostMapping
    public ResponseEntity<CommentRes> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentReq commentReq,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        //오류
        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = userPrincipal.id();

        CommentRes result = commentService.postComment(postId, userId, commentReq);

        URI location = URI.create(String.format("/api/posts/%d/comments/%d", postId, result.getCommentId()));

        return ResponseEntity.created(location).body(result);
    }


    //댓글 조회
    @GetMapping
    public ResponseEntity<Page<CommentRes>> getComments(
            @PathVariable Long postId,
            @PageableDefault(size=20, sort="createdAt") Pageable pageable) {

        Page<CommentRes> page = commentService.getCommentsByPost(postId, pageable);
        return ResponseEntity.ok(page);
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(userPrincipal.role());

        commentService.deleteComment(postId, commentId, userPrincipal.id(), isAdmin);
        return ResponseEntity.noContent().build();

    }
}