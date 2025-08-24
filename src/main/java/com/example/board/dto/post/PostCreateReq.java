package com.example.board.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/** 게시글 작성 요청 */
@Getter
public class PostCreateReq {

    @NotBlank private String title;
    @NotBlank private String content;
    private String imageUrl;
}
