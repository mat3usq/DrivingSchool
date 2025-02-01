package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "NOTIFICATION")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "CONTENT", length = 1000)
    private String content;

    @Column(name = "STATUS")
    private String status;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "SCHOOLUSERID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SchoolUser schoolUser;

    @CreatedDate
    @Column(name = "CREATEDAT")
    private LocalDateTime createdAt;

    public Notification(Long id, String content, String status, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
    }
}