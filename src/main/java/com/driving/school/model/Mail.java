package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "MAIL", indexes = {
        @Index(name = "idx_sender", columnList = "SENDERID"),
        @Index(name = "idx_recipient", columnList = "RECIPIENTID"),
        @Index(name = "idx_parent_mail", columnList = "PARENT_MAIL_ID"),
        @Index(name = "idx_created_at", columnList = "CREATED_AT")
})
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "SUBJECT", nullable = false)
    private String subject;

    @Column(name = "BODY", columnDefinition = "TEXT", nullable = false)
    private String body;

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SENDERID", nullable = false)
    private SchoolUser sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECIPIENTID", nullable = false)
    private SchoolUser recipient;

    @Column(name = "STATUS_RECIPIENT", nullable = false)
    private String statusRecipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_MAIL_ID")
    private Mail parentMail;

    @OneToMany(mappedBy = "parentMail", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Mail> replies = new ArrayList<>();
}
