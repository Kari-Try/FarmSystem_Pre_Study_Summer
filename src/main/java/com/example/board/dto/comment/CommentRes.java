package com.example.board.dto.comment;

import com.example.board.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentRes {

    private Long commentId;
    private Long postId;
    private String nickName; //작성자 닉네임
    private String content; //댓글 내용
    private LocalDateTime createdAt; //생성일

    //Entity->DTO 변환
    public static CommentRes fromEntity(Comment comment) {
        return CommentRes.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPost().getId())
                .nickName(comment.getUser().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
