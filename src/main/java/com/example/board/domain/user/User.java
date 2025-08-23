package com.example.board.domain.user;

import com.example.board.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_users_email_deleted",
                columnNames = {"email", "deleted"}
        )
)

@SQLRestriction("deleted = 0") // ✅ 권장
@SQLDelete(sql = "UPDATE users SET deleted = 1, deleted_at = now() WHERE id = ?")

public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false, length = 191) // MySQL 인덱스 길이 안전
    private String email;

    @Column(nullable = false)
    private String password; // BCrypt 해시

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean deleted = false;        // ✅ 전략 B

    private LocalDateTime deletedAt;
}
