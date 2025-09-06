package com.example.board.service;


import com.example.board.dto.post.PostDetailRes;
import com.example.board.dto.post.PostListRes;
import com.example.board.dto.post.PostListRow;
import com.example.board.repository.post.PostLikeRepository;
import com.example.board.repository.post.PostRepository;
import com.example.board.security.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostService postService;
    private final UserService userService;

    //로그아웃
    public void logout(String accessToken) {
        userService.logout(accessToken);
    }

    //회원탈퇴(soft delete)
    public void softDeleteMe(Long userId, String accessToken) {
        userService.softDeleteMe(userId, accessToken);
    }


    //내가 쓴 글 목록 조회
    @Transactional
    public Page<PostListRes> myPosts(Pageable pageable) {
        Long me = SecurityUtil.currentUserIdOrThrow();

        Page<PostListRow> page = postRepository.findListRowsByAuthorId(me, pageable);

        //좋아요 눌렀는지 표기
        Set<Long> likedIds = Set.of();
        if (!page.isEmpty()) {
            List<Long> ids = page.stream().map(PostListRow::id).toList();
            likedIds = new HashSet<>(postLikeRepository.findLikedPostIds(me, ids));
        }

        final Long finalME = me;
        final Set<Long> finalLikedIds = likedIds;

        return page.map(p -> PostListRes.builder()
                .id(p.id()).title(p.title())
                .authorName(p.authorName())
                .createdAt(p.createdAt()).updatedAt(p.updatedAt())
                .likeCount(p.likeCount()).commentCount(p.commentCount())
                .editable(true)
                .liked(finalLikedIds.contains(p.id()))
                .build());

    }

    //내가 쓴 글 상세
    @Transactional
    public PostDetailRes myPostDetail(Long postId) {

        PostDetailRes detailPost = postService.detail(postId);

        if(!detailPost.isEditable()) {
            throw new EntityNotFoundException("내 게시글이 아닙니다.");
        }

        return detailPost;
    }

    //내가 좋아요한 글 목록
    @Transactional
    public Page<PostListRes> myLikedPosts(Pageable pageable) {
        Long me = SecurityUtil.currentUserIdOrThrow();
        Page<PostListRow> page = postRepository.findListRowsLikedBy(me, pageable);

        return page.map(p->PostListRes.builder()
                .id(p.id())
                .title(p.title())
                .authorName(p.authorName())
                .createdAt(p.createdAt())
                .updatedAt(p.updatedAt())
                .likeCount(p.likeCount())
                .commentCount(p.commentCount())
                .editable(p.authorId().equals(me))
                .liked(true)
                .build());
    }

    //내가 좋아요한 글 상세
    @Transactional(readOnly = true)
    public PostDetailRes myLikedPostDetail(Long postId) {
        Long me = SecurityUtil.currentUserIdOrThrow();

        if(!postLikeRepository.existsByPostIdAndUserId(postId, me)) {
            throw new EntityNotFoundException("좋아요한 글이 아닙니다.");
        }

        return postService.detail(postId);
    }


}
