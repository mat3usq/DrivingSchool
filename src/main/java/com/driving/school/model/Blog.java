package com.driving.school.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "blog")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "content", length = 4096)
    private String content;

    @Column(name = "image")
    private byte[] image;

    @Column(name = "createdat")
    private LocalDate createdat;

    @Column(name = "createdby")
    private Integer createdby;

    @Column(name = "lastupdateat")
    private LocalDate lastupdateat;

    @Column(name = "lastupdateby")
    private Integer lastupdateby;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public LocalDate getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDate createdat) {
        this.createdat = createdat;
    }

    public Integer getCreatedby() {
        return createdby;
    }

    public void setCreatedby(Integer createdby) {
        this.createdby = createdby;
    }

    public LocalDate getLastupdateat() {
        return lastupdateat;
    }

    public void setLastupdateat(LocalDate lastupdateat) {
        this.lastupdateat = lastupdateat;
    }

    public Integer getLastupdateby() {
        return lastupdateby;
    }

    public void setLastupdateby(Integer lastupdateby) {
        this.lastupdateby = lastupdateby;
    }

}