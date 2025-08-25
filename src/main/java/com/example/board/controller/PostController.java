package com.example.board.controller;

import com.example.board.dto.post.*;
import com.example.board.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    /** 생성 */
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid PostCreateReq req) {
        return ResponseEntity.ok(postService.create(req));
    }

    /** 목록 */
    @GetMapping
    public ResponseEntity<Page<PostListRes>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        String[] parts = sort.split(",");
        String field = parts[0];
        boolean asc = parts.length > 1 && "asc".equalsIgnoreCase(parts[1]);
        Sort s = asc ? Sort.by(field).ascending() : Sort.by(field).descending();
        Pageable pageable = PageRequest.of(page, size, s);
        return ResponseEntity.ok(postService.list(pageable));
    }

    /** 상세 */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailRes> detail(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.detail(postId));
    }

    /** 수정 */
    @PutMapping("/{postId}")
    public ResponseEntity<Long> update(@PathVariable Long postId, @RequestBody @Valid PostUpdateReq req) {
        return ResponseEntity.ok(postService.update(postId, req));
    }

    /** 삭제 */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }

    /** 좋아요 등록 */
    @PutMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> like(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.setLike(postId, true));
    }

    /** 좋아요 취소 */
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> unlike(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.setLike(postId, false));
    }

}
