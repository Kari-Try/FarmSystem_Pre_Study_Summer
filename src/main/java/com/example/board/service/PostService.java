package com.example.board.service;

import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostLike;
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

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    /** 생성 */
    public Long create(PostCreateReq req) {
        Long me = SecurityUtil.currentUserIdOrThrow();
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

    /** 목록 */
    public Page<PostListRes> list(Pageable pageable) {
        Long me = null;
        try { me = SecurityUtil.currentUserIdOrThrow(); } catch (Exception ignored) {}

        Page<PostListRow> page = postRepository.findListRows(pageable);

        Set<Long> likedIds = Set.of();
        if (me != null && !page.isEmpty()) {
            List<Long> ids = page.stream().map(PostListRow::id).toList();
            likedIds = new HashSet<>(postLikeRepository.findLikedPostIds(me, ids));
        }

        final Long finalMe = me;
        final Set<Long> finalLikedIds = likedIds;

        return page.map(p -> PostListRes.builder()
                .id(p.id())
                .title(p.title())
                .authorName(p.authorName())
                .createdAt(p.createdAt())
                .updatedAt(p.updatedAt())
                .likeCount(p.likeCount())
                .commentCount(p.commentCount())
                .editable(finalMe != null && p.authorId().equals(finalMe))
                .liked(finalMe != null && finalLikedIds.contains(p.id()))
                .build());
    }

    /** 상세 */
    public PostDetailRes detail(Long postId) {
        Post p = postRepository.findById(postId).orElseThrow();
        Long me = null;
        try { me = SecurityUtil.currentUserIdOrThrow(); } catch (Exception ignored) {}

        String authorName = p.getAuthor().getNickname() != null
                ? p.getAuthor().getNickname()
                : p.getAuthor().getEmail();

        boolean liked = (me != null) &&
                postLikeRepository.findByPostIdAndUserId(postId, me).isPresent();

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
                .liked(liked)
                .build();
    }

    /** 수정 */
    public Long update(Long postId, PostUpdateReq req) {
        Long me = SecurityUtil.currentUserIdOrThrow();
        Post p = postRepository.findById(postId).orElseThrow();
        if (!p.isOwnedBy(me)) throw new IllegalStateException("작성자가 아닙니다");
        p.applyUpdate(req.getTitle(), req.getContent(), req.getImageUrl());
        return p.getId();
    }

    /** 삭제 */
    public void delete(Long postId) {
        Long me = SecurityUtil.currentUserIdOrThrow();
        Post p = postRepository.findById(postId).orElseThrow();
        if (!p.isOwnedBy(me)) throw new IllegalStateException("작성자가 아닙니다");
        postRepository.delete(p);
    }

    /** 좋아요 */
    public Map<String, Object> setLike(Long postId, boolean like) {
        Long me = SecurityUtil.currentUserIdOrThrow();
        Post post = postRepository.findById(postId).orElseThrow();
        User user = userRepository.findById(me).orElseThrow();

        Optional<PostLike> existing = postLikeRepository.findByPostIdAndUserId(postId, me);
        boolean nowLiked = existing.isPresent();

        if (like && !nowLiked) {
            postLikeRepository.save(PostLike.builder().post(post).user(user).build());
            postRepository.updateLikeCount(postId, +1);
            nowLiked = true;
        } else if (!like && nowLiked) {
            postLikeRepository.delete(existing.get());
            postRepository.updateLikeCount(postId, -1);
            nowLiked = false;
        }
        int likeCount = post.getLikeCount(); // 엔티티 1차 캐시에 반영되어 있음
        return Map.of("liked", nowLiked, "likeCount", likeCount);
    }


}
