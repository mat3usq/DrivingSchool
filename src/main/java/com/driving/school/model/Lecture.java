package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "LECTURE")
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 1024)
    private String name;

    @Column(name = "CONTENT", length = 4096)
    private String content;

    @Column(name = "ORDERINDEX", nullable = false)
    private Integer orderIndex;

    @OneToMany(mappedBy = "lecture")
    private List<Sublecture> sublectures = new ArrayList<>();

    @Column(name = "CATEGORY")
    private String category;

    public Lecture(String name, String content, Integer orderIndex, String category) {
        this.name = name;
        this.content = content;
        this.orderIndex = orderIndex;
        this.category = category;
    }
}