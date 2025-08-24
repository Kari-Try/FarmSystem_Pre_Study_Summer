package com.example.board.domain.comment;

import com.example.board.domain.post.Post;
import com.example.board.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //유저 외래키

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; //게시글 외래키

    @Column(nullable = false, length = 1000)
    private String content; //댓글 내용

    @Column(nullable = false)
    private LocalDateTime createdAt; //생성 일자


}