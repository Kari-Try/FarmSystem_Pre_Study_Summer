package com.example.board.dto.post;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/** 목록 1건 응답 DTO (프리뷰 제거, liked/editable 포함) */
@Getter
@Builder
public class PostListRes {
    private Long id;
    private String title;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;
    private int commentCount;
    private boolean editable;  // 내 글 여부
    private boolean liked;     // 내가 좋아요 눌렀는지
}
