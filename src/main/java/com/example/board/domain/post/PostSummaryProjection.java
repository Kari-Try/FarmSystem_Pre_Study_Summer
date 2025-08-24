package com.example.board.domain.post;


import java.time.LocalDateTime;

public interface PostSummaryProjection {
    Long getId();
    String getTitle();
    String getAuthorName();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    int getLikeCount();
    int getCommentCount();
    Long getAuthorId();
}
