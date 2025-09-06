package com.example.board.repository.post;

import com.example.board.domain.post.PostLike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


/**
 * 좋아요 레포지토리
 * - findByPostAndUser: 특정 사용자가 해당 글에 좋아요 눌렀는지 여부 조회
 * - countByPost: 특정 글의 좋아요 수 조회
 */

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    @Query("select pl.post.id from PostLike pl where pl.user.id = :userId and pl.post.id in :postIds")
    List<Long> findLikedPostIds(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);


    boolean existsByPostIdAndUserId(Long postId, Long me);
}