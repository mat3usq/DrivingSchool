package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

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

    @ManyToMany(mappedBy = "categories")
    private List<Payment> payments;

    @ElementCollection
    @CollectionTable(
            name = "CATEGORY_AUTHORIZATIONS",
            joinColumns = @JoinColumn(name = "CATEGORY_ID")
    )
    @Column(name = "AUTHORIZATION", columnDefinition = "TEXT")
    private List<String> authorization;

    @Column(name = "MINIMUM_AGE", nullable = false)
    private String minimumAge;

    public Category(String nameCategory, List<SchoolUser> schoolUsers, String minimumAge, List<String> authorizations) {
        this.nameCategory = nameCategory;
        this.schoolUsers = schoolUsers;
        this.authorization = authorizations;
        this.minimumAge = minimumAge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(nameCategory, category.nameCategory);
    }
}
