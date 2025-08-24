package com.example.board.domain.post;

import com.example.board.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * 특정 게시글(Post)와 특정 사용자(User) 사이의 좋아요 관계를 저장하는 엔티티
 * (post_id, user_id) 유니크 제약 조건으로 중복 좋아요 방지
 */
@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
public class PostLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
