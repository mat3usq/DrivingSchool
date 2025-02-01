package com.driving.school.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
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

    @NotBlank(message = "Temat jest wymagany")
    @Size(max = 100, message = "Temat może mieć maksymalnie 100 znaków")
    @Column(name = "SUBJECT", length = 100, nullable = false)
    private String subject;

    @NotBlank(message = "Treść wiadomosci jest wymagana")
    @Column(name = "BODY", columnDefinition = "TEXT", nullable = false)
    private String body;

    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SENDERID", nullable = false)
    private SchoolUser sender;

    @Column(name = "STATUS_SENDER")
    private String statusSender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECIPIENTID", nullable = false)
    private SchoolUser recipient;

    @Column(name = "STATUS_RECIPIENT")
    private String statusRecipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_MAIL_ID")
    private Mail parentMail;

    @OneToMany(mappedBy = "parentMail", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Mail> replies = new ArrayList<>();

    public Mail(String body) {
        this.body = body;
    }
}
