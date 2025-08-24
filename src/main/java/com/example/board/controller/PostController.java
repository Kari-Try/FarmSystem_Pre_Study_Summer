package com.example.board.controller;

import com.example.board.dto.post.*;
import com.example.board.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 게시글 API 컨트롤러
 * - /api/posts 경로 아래에서 동작
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    /** 게시글 작성 */
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid PostCreateReq req) {
        return ResponseEntity.ok(postService.create(req));
    }

    /** 게시글 목록 조회 */
    @GetMapping
    public ResponseEntity<Page<PostListRes>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        Sort s = sort.endsWith(",asc") || sort.endsWith(",desc")
                ? Sort.by(sort.split(",")[0]).descending()
                : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, s);
        return ResponseEntity.ok(postService.list(pageable));
    }

    /** 게시글 단건 상세 조회 */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailRes> detail(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.detail(postId));
    }

    /** 게시글 수정 */
    @PutMapping("/{postId}")
    public ResponseEntity<Long> update(@PathVariable Long postId, @RequestBody @Valid PostUpdateReq req) {
        return ResponseEntity.ok(postService.update(postId, req));
    }

    /** 게시글 삭제 */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }

    /** 좋아요 토글 */
    @PostMapping("/{postId}/like")
    public ResponseEntity<Boolean> toggleLike(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.toggleLike(postId));
    }
}
