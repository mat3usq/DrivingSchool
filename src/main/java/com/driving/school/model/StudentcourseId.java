package com.driving.school.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StudentcourseId implements Serializable {
    private static final long serialVersionUID = 3623306026785572117L;
    @Column(name = "userid", nullable = false)
    private Integer userid;

    @Column(name = "courseid", nullable = false)
    private Integer courseid;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getCourseid() {
        return courseid;
    }

    public void setCourseid(Integer courseid) {
        this.courseid = courseid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudentcourseId entity = (StudentcourseId) o;
        return Objects.equals(this.userid, entity.userid) &&
                Objects.equals(this.courseid, entity.courseid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, courseid);
    }

}