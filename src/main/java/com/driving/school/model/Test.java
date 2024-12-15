package com.driving.school.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "TEST")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", length = 512)
    private String name;

    @Column(name = "CATEGORY")
    private String drivingCategory;

    @Column(name = "NUMBERQUESTIONS")
    private Integer numberQuestions;

    @Column(name = "IMAGE", columnDefinition = "LONGBLOB")
    private byte[] image;

    @Column(name = "TESTTYPE", length = 512)
    private Boolean testType;

    @Transient
    private Integer counter;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "QUESTIONTEST",
            joinColumns = @JoinColumn(name = "TESTID"),
            inverseJoinColumns = @JoinColumn(name = "QUESTIONID")
    )
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "test")
    private List<StudentAnswersTest> studentAnswersTests = new ArrayList<>();

    public String getImageBase64() {
        return (this.image != null) ? "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(this.image) : null;
    }

    @Override
    public String toString() {
        return "Test{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", drivingCategory='" + drivingCategory + '\'' +
                ", numberQuestions=" + numberQuestions +
                ", testType=" + testType +
                ", counter=" + counter +
                '}';
    }
}