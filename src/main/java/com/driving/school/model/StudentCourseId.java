package com.driving.school.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class StudentCourseId implements Serializable {
    private static final long serialVersionUID = 957706009752532724L;
    @Column(name = "SCHOOLUSERID", nullable = false)
    private Long schooluserid;

    @Column(name = "COURSEID", nullable = false)
    private Long courseid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudentCourseId entity = (StudentCourseId) o;
        return Objects.equals(this.schooluserid, entity.schooluserid) &&
                Objects.equals(this.courseid, entity.courseid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schooluserid, courseid);
    }

}