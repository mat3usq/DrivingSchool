package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "SUBJECT")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "SUBJECT", length = 1024)
    private String title;

    @Column(name = "CONTENT", length = 4096)
    private String content;

    @Column(name = "IMAGE", columnDefinition = "LONGBLOB")
    private byte[] image;

    @Column(name = "ORDERINDEX", nullable = false)
    private Integer orderIndex;

    @Transient
    private MultipartFile file;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "SUBLECTUREID", nullable = false)
    private Sublecture sublecture;

    public String getImageBase64() {
        return (this.image != null) ? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(this.image) : null;
    }

    public Subject(String title, String content, byte[] image, Integer orderIndex, Sublecture sublecture) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.orderIndex = orderIndex;
        this.sublecture = sublecture;
    }
}