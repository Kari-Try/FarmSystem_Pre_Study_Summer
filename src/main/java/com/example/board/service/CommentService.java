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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;


import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    //댓글 작성
    //DTO->Entity 변환하여 DB에 데이터 저장
    @Transactional
    public CommentRes postComment(Long postId, Long userId, CommentReq commentReq) {

        //사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("userId=" + userId + " : 존재하지 않는 사용자입니다."));

        //댓글 작성한 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("postId=" + postId + " : 존재하지 않는 게시글입니다."));

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content(commentReq.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        return CommentRes.fromEntity(comment);
    }


    //해당 게시글 댓글 조회
    //Entity->DTO 변환하여 read
    @Transactional(readOnly = true)
    public Page<CommentRes> getCommentsByPost(Long postId, Pageable pageable) {
        if(!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("postId=" + postId + ":존재하지 않는 게시글입니다.");
        }

        return commentRepository.findByPost_Id(postId, pageable)
                .map(CommentRes::fromEntity);
    }



    //댓글 삭제
    @Transactional
    public void deleteComment(Long postId, Long commentId, Long requesterUserId, boolean isAdmin) {

        //댓글이 존재하는가?
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("commentId=" + commentId + " : 해당 댓글을 찾을 수 없습니다."));

        //게시글 확인
        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("게시글 정보가 일치하지 않아 댓글을 삭제할 수 없습니다.");
        }

        //본인이 작성한 댓글만 삭제 가능
        if (!isAdmin && !comment.getUser().getId().equals(requesterUserId)) {
            throw new AccessDeniedException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);

    }
}
