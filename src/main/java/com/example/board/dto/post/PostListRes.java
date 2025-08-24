package com.example.board.dto.post;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** 게시글 목록 조회 응답 */
@Getter @Builder
public class PostListRes {
    private Long id;
    private String title;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;
    private int commentCount;
    private boolean editable;
}
