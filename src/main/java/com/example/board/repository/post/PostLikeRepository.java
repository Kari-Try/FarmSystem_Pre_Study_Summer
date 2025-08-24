package com.example.board.repository.post;

import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostLike;
import com.example.board.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 좋아요 레포지토리
 * - findByPostAndUser: 특정 사용자가 해당 글에 좋아요 눌렀는지 여부 조회
 * - countByPost: 특정 글의 좋아요 수 조회
 */
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
    long countByPost(Post post);
}
