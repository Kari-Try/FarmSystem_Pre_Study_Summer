package com.example.board.dto.post;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/** 게시글 상세 조회 응답 */
@Getter @Builder
public class PostDetailRes {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;
    private int commentCount;
    private boolean editable;
}
