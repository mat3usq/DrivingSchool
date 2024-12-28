package com.driving.school.model;

import com.driving.school.annotations.FieldsValueMatch;
import com.driving.school.annotations.PasswordValidator;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@FieldsValueMatch.List({
        @FieldsValueMatch(
                field = "password",
                fieldMatch = "confirmPassword",
                message = "Hasła nie pasuja do siebie."
        ),
})
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "SCHOOLUSER")
public class SchoolUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @NotBlank(message = "Imie nie moze byc puste.")
    @Size(min = 3, message = "Imie nie moze byc krótsze niż 3 litery.")
    @Column(name = "NAME", length = 64)
    private String name;

    @NotBlank(message = "Nazwisko nie moze byc puste.")
    @Size(min = 2, message = "Nazwisko nie moze byc krótsze niż 2 litery.")
    @Column(name = "SURNAME", length = 64)
    private String surname;

    @Size(min = 5, message = "Hasło musi zawierac conajmniej 5 znaków.")
    @PasswordValidator
    @Column(name = "PASSWORD", length = 256)
    private String password;

    @Size(min = 5, message = "Hasło musi zawierac conajmniej 5 znaków.")
    @Transient
    private String confirmPassword;

    @NotBlank(message = "Email nie moze byc pusty")
    @Email(message = "Wprowadz poprawny email.")
    @Column(name = "EMAIL", length = 64)
    private String email;

    @Column(name = "ROLENAME", length = 128)
    private String roleName;

    @Column(name = "NUMBEROFMAILS")
    private int numberOfMails = 0;

    @ManyToMany(mappedBy = "students")
    private List<InstructionEvent> studentEvents = new ArrayList<>();

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "schoolUser")
    private Set<Payment> payments = new LinkedHashSet<>();

    @Column(name = "NUMBEROFNOTIFICATIONS")
    private int numberOfNotifications = 0;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "schoolUser", fetch = FetchType.EAGER)
    private List<Notification> notifications = new LinkedList<>();

    @OneToMany(mappedBy = "schoolUser")
    private Set<StudentAnswersTest> studentAnswersTests = new LinkedHashSet<>();

    @OneToMany(mappedBy = "schoolUser")
    private Set<StudentExam> studentExams = new LinkedHashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "USER_ID")
    private Set<UserLikedQuestion> likedQuestions = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "SCHOOLUSER_CATEGORY",
            joinColumns = @JoinColumn(name = "SCHOOLUSERID"),
            inverseJoinColumns = @JoinColumn(name = "CATEGORYID")
    )
    private List<Category> availableCategories = new ArrayList<>();

    @Column(name = "CURRENTCATEGORY")
    private String currentCategory;

    private String selectedTypeQuestions = "remainingQuestions";

    @Column(name = "TOKEN_FOR_RESET_PASSWORD")
    private String tokenForResettingPassword;

    @Column(name = "TOKEN_EXPIRY")
    private LocalDateTime tokenExpiry;

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

    public SchoolUser(String name, String surname, String password, String email, String roleName, String currentCategory) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
        this.roleName = roleName;
        this.currentCategory = currentCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolUser that = (SchoolUser) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(surname, that.surname) && Objects.equals(email, that.email) && Objects.equals(roleName, that.roleName);
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
                ", studentAnswersTests=" + studentAnswersTests +
                ", studentExams=" + studentExams +
                ", createdAt=" + createdAt +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdatedAt=" + lastUpdatedAt +
                ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
                '}';
    }
}