package com.example.board.service;

import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostLike;
import com.example.board.domain.post.PostSummaryProjection;
import com.example.board.domain.user.User;
import com.example.board.dto.post.*;
import com.example.board.repository.post.PostLikeRepository;
import com.example.board.repository.post.PostRepository;
import com.example.board.repository.user.UserRepository;
import com.example.board.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


/**
 * 게시글 서비스
 * - create, update, delete: 로그인 유저 권한 확인
 * - list, detail: editable 여부 표시
 * - toggleLike: 좋아요 on/off
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    /** 게시글 작성 */
    public Long create(PostCreateReq req) {
        Long me = SecurityUtil.currentUserIdOrThrow(); // 로그인한 사용자 ID
        User author = userRepository.findById(me).orElseThrow();
        Post post = Post.builder()
                .author(author)
                .title(req.getTitle())
                .content(req.getContent())
                .imageUrl(req.getImageUrl())
                .likeCount(0)
                .commentCount(0)
                .build();
        return postRepository.save(post).getId();
    }

    /** 게시글 목록 조회 */
    public Page<PostListRes> list(Pageable pageable) {
        Long me = null;
        try { me = SecurityUtil.currentUserIdOrThrow(); } catch (Exception ignored) {}
        Page<PostSummaryProjection> page = postRepository.findSummaries(pageable);
        Long finalMe = me;
        return page.map(p -> PostListRes.builder()
                .id(p.getId())
                .title(p.getTitle())
                .authorName(p.getAuthorName())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .likeCount(p.getLikeCount())
                .commentCount(p.getCommentCount())
                .editable(finalMe != null && p.getAuthorId().equals(finalMe))
                .build());
    }

    /** 게시글 상세 조회 */
    public PostDetailRes detail(Long postId) {
        Post p = postRepository.findById(postId).orElseThrow();
        Long me = null;
        try { me = SecurityUtil.currentUserIdOrThrow(); } catch (Exception ignored) {}
        String authorName = p.getAuthor().getNickname() != null
                ? p.getAuthor().getNickname()
                : p.getAuthor().getEmail();

        return PostDetailRes.builder()
                .id(p.getId())
                .title(p.getTitle())
                .content(p.getContent())
                .imageUrl(p.getImageUrl())
                .authorName(authorName)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .likeCount(p.getLikeCount())
                .commentCount(p.getCommentCount())
                .editable(me != null && p.isOwnedBy(me))
                .build();
    }

    /** 게시글 수정 */
    public Long update(Long postId, PostUpdateReq req) {
        Long me = SecurityUtil.currentUserIdOrThrow();
        Post p = postRepository.findById(postId).orElseThrow();
        if (!p.isOwnedBy(me)) throw new IllegalStateException("작성자가 아닙니다");
        p.applyUpdate(req.getTitle(), req.getContent(), req.getImageUrl());
        return p.getId();
    }

    /** 게시글 삭제 */
    public void delete(Long postId) {
        Long me = SecurityUtil.currentUserIdOrThrow();
        Post p = postRepository.findById(postId).orElseThrow();
        if (!p.isOwnedBy(me)) throw new IllegalStateException("작성자가 아닙니다");
        postRepository.delete(p);
    }

    /** 좋아요 토글: true=좋아요됨, false=해제됨 */
    public boolean toggleLike(Long postId) {
        Long me = SecurityUtil.currentUserIdOrThrow();
        Post post = postRepository.findById(postId).orElseThrow();
        User user = userRepository.findById(me).orElseThrow();

        return postLikeRepository.findByPostAndUser(post, user)
                .map(existing -> {
                    postLikeRepository.delete(existing);
                    postRepository.updateLikeCount(postId, -1);
                    return false; // 좋아요 취소
                })
                .orElseGet(() -> {
                    postLikeRepository.save(PostLike.builder().post(post).user(user).build());
                    postRepository.updateLikeCount(postId, +1);
                    return true; // 좋아요 등록
                });
    }
}
