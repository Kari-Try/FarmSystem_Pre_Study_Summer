package com.example.board.controller;


import com.example.board.dto.comment.CommentReq;
import com.example.board.dto.comment.CommentRes;
import com.example.board.security.UserPrincipal;
import com.example.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    //댓글 생성
    @PostMapping("/comment")
    public ResponseEntity<?> createComment(@RequestBody CommentReq commentReq,
                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        //오류
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body("인증이 필요합니다.");
        }

        Long userId = userPrincipal.id();
        CommentRes result = commentService.postComment(commentReq, userId);
        return ResponseEntity.ok(result);
    }


    //댓글 조회
    @GetMapping("/post/{postId}/comment")
    public List<CommentRes> getComment(@PathVariable("postId") Long postId) {
        List<CommentRes> commentList;
        commentList = commentService.getCommentsByPost(postId);

        return commentList;
    }

    //댓글 삭제
    @DeleteMapping("/post/{postId}/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(postId, commentId);
        return ResponseEntity.noContent().build();
    }
}