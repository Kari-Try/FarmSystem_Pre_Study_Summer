package com.example.board.repository.comment;

import com.example.board.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    //특정 게시글 조회
    List<Comment> findByPost_IdOrderByCreatedAtAsc(Long postId);


}