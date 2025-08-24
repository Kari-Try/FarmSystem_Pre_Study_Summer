package com.example.board.service;


import com.example.board.domain.post.Post;
import com.example.board.domain.user.User;
import com.example.board.dto.comment.CommentReq;
import com.example.board.dto.comment.CommentRes;
import com.example.board.domain.comment.Comment;
import com.example.board.repository.comment.CommentRepository;
import com.example.board.repository.post.PostRepository;
import com.example.board.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    //댓글 작성
    //DTO->Entity 변환하여 DB에 데이터 저장
    @Transactional
    public CommentRes postComment(CommentReq commentReq, Long userId) {

        //사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("userId=" + userId + " : 존재하지 않는 사용자입니다."));

        //댓글 작성한 게시글 조회
        Post post = postRepository.findById(commentReq.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("postId=" + commentReq.getPostId() + " : 존재하지 않는 게시글입니다."));

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(commentReq.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        return CommentRes.from(comment);
    }


    //해당 게시글 댓글 조회
    //Entity->DTO 변환하여 read
    @Transactional(readOnly = true)
    public List<CommentRes> getCommentsByPost(Long postId) {
        //post 존재 여부만 검증
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("postId=" + postId + " : 존재하지 않는 게시글입니다.");
        }

        //글 조회
        List<Comment> commentList = commentRepository.findByPost_IdOrderByCreatedAtAsc(postId);

        //Entity -> DTO 변환
        List<CommentRes> commentResList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentRes commentRes = CommentRes.from(comment);
            commentResList.add(commentRes);
        }

        return commentResList;
    }



    //댓글 삭제
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        //댓글이 존재하는가?
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("commentId=" + commentId + " : 해당 댓글을 찾을 수 없습니다."));

        //찾은 댓글이 올바른 게시글에 속해 있는가?
        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("게시글 정보가 일치하지 않아 댓글을 삭제할 수 없습니다. ");
        }

        commentRepository.delete(comment);

    }
}
