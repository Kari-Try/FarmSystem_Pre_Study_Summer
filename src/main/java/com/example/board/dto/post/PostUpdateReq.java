package com.example.board.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/** 게시글 수정 요청 */
@Getter
public class PostUpdateReq {
    @NotBlank private String title;
    @NotBlank private String content;
    private String imageUrl;
}
