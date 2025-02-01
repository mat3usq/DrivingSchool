package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "SUBLECTURE")
public class Sublecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "TITLE", length = 1024)
    private String title;

    @Column(name = "CONTENT", length = 4096)
    private String content;

    @Column(name = "ORDERINDEX", nullable = false)
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "LECTUREID", nullable = false)
    private Lecture lecture;

    @OneToMany(mappedBy = "sublecture")
    private List<Subject> subjects = new ArrayList<>();

    public Sublecture(String title, String content, Integer orderIndex, Lecture lecture) {
        this.title = title;
        this.content = content;
        this.orderIndex = orderIndex;
        this.lecture = lecture;
    }
}