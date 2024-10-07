package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "SCHOOLUSER")
public class SchoolUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", length = 64)
    private String name;

    @Column(name = "SURNAME", length = 64)
    private String surname;

    @Column(name = "PASSWORD", length = 256)
    private String password;

    @Column(name = "EMAIL", length = 64)
    private String email;

    @Column(name = "ROLENAME", length = 128)
    private String roleName;

    @ManyToMany(mappedBy = "students")
    private List<InstructionEvent> studentEvents = new ArrayList<>();

    @OneToMany(mappedBy = "schoolUser")
    private Set<Payment> payments = new LinkedHashSet<>();

    @OneToOne(mappedBy = "schoolUser")
    private SensitiveData sensitiveData;

    @OneToMany(mappedBy = "schoolUser")
    private Set<StudentAnswersTest> studentAnswersTests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "schoolUser")
    private Set<StudentCourse> studentCourses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "schoolUser")
    private Set<StudentExam> studentExams = new LinkedHashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID")
    private Set<UserLikedQuestion> likedQuestions = new LinkedHashSet<>();

    @OneToOne(mappedBy = "schoolUser")
    private UserStatistic userStatistic;

    @Column(name = "AVAILABLECATEGORIES")
    private String availableCategories;

    @Column(name = "CURRENTCATEGORY")
    private String currentCategory;

    private String selectedTypeQuestions = "remainingQuestions";

    @CreatedDate
    @Column(name = "CREATEDAT")
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "CREATEDBY", length = 64)
    private String createdBy;

    @LastModifiedDate
    @Column(name = "LASTUPDATEDAT")
    private LocalDateTime lastUpdatedAt;

    @LastModifiedBy
    @Column(name = "LASTUPDATEDBY", length = 64)
    private String lastUpdatedBy;

    public SchoolUser(String name, String surname, String password, String email, String roleName) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "SchoolUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", roleName='" + roleName + '\'' +
                ", studentEvents=" + studentEvents +
                ", payments=" + payments +
                ", sensitiveData=" + sensitiveData +
                ", studentAnswersTests=" + studentAnswersTests +
                ", studentCourses=" + studentCourses +
                ", studentExams=" + studentExams +
                ", userStatistic=" + userStatistic +
                ", createdAt=" + createdAt +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdatedAt=" + lastUpdatedAt +
                ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
                '}';
    }
}