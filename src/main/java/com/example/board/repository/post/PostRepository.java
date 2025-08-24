package com.example.board.repository.post;

import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostSummaryProjection;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

/**
 * 게시글 레포지토리
 * - 목록 조회: DTO 생성자 표현식으로 안전하게 반환
 * - 좋아요/댓글 수 업데이트 메서드 제공
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
        select p.id as id,
               p.title as title,
               coalesce(u.nickname, u.email) as authorName,
               p.createdAt as createdAt,
               p.updatedAt as updatedAt,
               p.likeCount as likeCount,
               p.commentCount as commentCount,
               u.id as authorId
        from Post p
        join p.author u
        """)
    Page<PostSummaryProjection> findSummaries(Pageable pageable);

    @Modifying
    @Query("update Post p set p.likeCount = p.likeCount + :delta where p.id = :postId")
    int updateLikeCount(@Param("postId") Long postId, @Param("delta") int delta);

    @Modifying
    @Query("update Post p set p.commentCount = p.commentCount + :delta where p.id = :postId")
    int updateCommentCount(@Param("postId") Long postId, @Param("delta") int delta);
}
