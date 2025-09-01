package com.example.board.repository.comment;

import com.example.board.domain.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    //게시글 페이지
    Page<Comment> findByPost_Id(Long postId, Pageable pageable);

}