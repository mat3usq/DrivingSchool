package com.driving.school.model;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "question", length = 512)
    private String question;

    @Column(name = "answera", length = 512)
    private String answera;

    @Column(name = "answerb", length = 512)
    private String answerb;

    @Column(name = "answerc", length = 512)
    private String answerc;

    @Column(name = "availablequestion")
    private Integer availablequestion;

    @Column(name = "correctanswer")
    private Integer correctanswer;

    @Column(name = "medianame", length = 512)
    private String medianame;

    @Column(name = "explanation", length = 1024)
    private String explanation;

    @Column(name = "questiontype", length = 512)
    private String questiontype;

    @Column(name = "category", length = 128)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "testid", nullable = false)
    private Test testid;

    @ManyToMany(mappedBy = "questions")
    private Set<Exam> exams = new LinkedHashSet<>();

    @OneToMany(mappedBy = "question")
    private Set<Studentanswerstest> studentanswerstests = new LinkedHashSet<>();

    @OneToOne(mappedBy = "question")
    private Studentexamanswer studentexamanswer;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswera() {
        return answera;
    }

    public void setAnswera(String answera) {
        this.answera = answera;
    }

    public String getAnswerb() {
        return answerb;
    }

    public void setAnswerb(String answerb) {
        this.answerb = answerb;
    }

    public String getAnswerc() {
        return answerc;
    }

    public void setAnswerc(String answerc) {
        this.answerc = answerc;
    }

    public Integer getAvailablequestion() {
        return availablequestion;
    }

    public void setAvailablequestion(Integer availablequestion) {
        this.availablequestion = availablequestion;
    }

    public Integer getCorrectanswer() {
        return correctanswer;
    }

    public void setCorrectanswer(Integer correctanswer) {
        this.correctanswer = correctanswer;
    }

    public String getMedianame() {
        return medianame;
    }

    public void setMedianame(String medianame) {
        this.medianame = medianame;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getQuestiontype() {
        return questiontype;
    }

    public void setQuestiontype(String questiontype) {
        this.questiontype = questiontype;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Test getTestid() {
        return testid;
    }

    public void setTestid(Test testid) {
        this.testid = testid;
    }

    public Set<Exam> getExams() {
        return exams;
    }

    public void setExams(Set<Exam> exams) {
        this.exams = exams;
    }

    public Set<Studentanswerstest> getStudentanswerstests() {
        return studentanswerstests;
    }

    public void setStudentanswerstests(Set<Studentanswerstest> studentanswerstests) {
        this.studentanswerstests = studentanswerstests;
    }

    public Studentexamanswer getStudentexamanswer() {
        return studentexamanswer;
    }

    public void setStudentexamanswer(Studentexamanswer studentexamanswer) {
        this.studentexamanswer = studentexamanswer;
    }

}