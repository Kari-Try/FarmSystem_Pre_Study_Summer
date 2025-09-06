package com.example.board.dto.post;

import java.time.LocalDateTime;

/** 목록 쿼리 결과를 최소 필드만 담아오는 내부 DTO */
public record PostListRow(
        Long id,
        String title,
        String authorName,
        Long authorId,     // editable 계산용
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer likeCount,
        Integer commentCount

) {}
