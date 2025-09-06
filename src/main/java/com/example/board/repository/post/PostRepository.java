package com.example.board.repository.post;

import com.example.board.domain.post.Post;
import com.example.board.dto.post.PostListRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 게시글 레포지토리
 * - 목록 조회: DTO 생성자 표현식으로 안전하게 반환
 * - 좋아요/댓글 수 업데이트 메서드 제공
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
        select new com.example.board.dto.post.PostListRow(
            p.id,
            p.title,
            coalesce(u.nickname, u.email),
            u.id,
            p.createdAt,
            p.updatedAt,
            p.likeCount,
            p.commentCount
        )
        from Post p join p.author u
        order by p.createdAt desc
        """)
    Page<PostListRow> findListRows(Pageable pageable);

    @Modifying
    @Query("update Post p set p.likeCount = p.likeCount + :delta where p.id = :postId")
    int updateLikeCount(@Param("postId") Long postId, @Param("delta") int delta);

    @Modifying
    @Query("update Post p set p.commentCount = p.commentCount + :delta where p.id = :postId")
    int updateCommentCount(@Param("postId") Long postId, @Param("delta") int delta);

    @Query("""
    select new com.example.board.dto.post.PostListRow(
      p.id,
      p.title,
      coalesce(p.author.nickname, p.author.email),
      p.author.id,
      p.createdAt,
      p.updatedAt,
      p.likeCount,
      p.commentCount
    )
    from Post p
    where p.author.id = :userId
    order by p.createdAt desc
    """)
    Page<PostListRow> findListRowsByAuthorId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
    select new com.example.board.dto.post.PostListRow(
      p.id,
      p.title,
      coalesce(p.author.nickname, p.author.email),
      p.author.id,
      p.createdAt,
      p.updatedAt,
      p.likeCount,
      p.commentCount
    )
    from PostLike pl
    join pl.post p
    where pl.user.id = :userId
    order by pl.createdAt desc
    """)
    Page<PostListRow> findListRowsLikedBy(@Param("userId") Long userId, Pageable pageable);
}
