package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "CATEGORY")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAMECATEGORY")
    private String nameCategory;

    @ManyToMany(mappedBy = "availableCategories")
    private List<SchoolUser> schoolUsers;

    public Category(String nameCategory, List<SchoolUser> schoolUsers) {
        this.nameCategory = nameCategory;
        this.schoolUsers = schoolUsers;
    }
}
