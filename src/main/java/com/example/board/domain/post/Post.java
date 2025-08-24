package com.example.board.domain.post;

import com.example.board.domain.common.BaseTimeEntity;
import com.example.board.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * 게시글 엔티티
 * BaseTimeEntity를 상속해서 createdAt, updatedAt 자동 관리
 */

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 작성자 (User)와 N:1 관계 */
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob @Column(nullable = false)
    private String content;

    /** 업로드된 이미지 경로 (옵션) */
    private String imageUrl;

    /** 좋아요 수 (카운터 컬럼) */
    @Column(nullable = false)
    private int likeCount;

    /** 댓글 수 (카운터 컬럼) */
    @Column(nullable = false)
    private int commentCount;

    /** 글 작성자가 현재 로그인한 사용자와 같은지 여부 */
    public boolean isOwnedBy(Long userId){
        return author != null && author.getId().equals(userId);
    }

    /** 수정 시 업데이트 */
    public void applyUpdate(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
