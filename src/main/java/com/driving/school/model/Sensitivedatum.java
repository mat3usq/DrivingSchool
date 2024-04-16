package com.driving.school.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "sensitivedata", indexes = {
        @Index(name = "sensitivedata__idx", columnList = "userid")
})
public class Sensitivedatum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "address", length = 64)
    private String address;

    @Column(name = "city", length = 64)
    private String city;

    @Column(name = "streetnumber")
    private Integer streetnumber;

    @Column(name = "apartmentnumber")
    private Integer apartmentnumber;

    @Column(name = "zipcode", length = 64)
    private String zipcode;

    @Column(name = "phonenumber", length = 15)
    private String phonenumber;

    @Column(name = "email", length = 64)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userid", nullable = false)
    private User userid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getStreetnumber() {
        return streetnumber;
    }

    public void setStreetnumber(Integer streetnumber) {
        this.streetnumber = streetnumber;
    }

    public Integer getApartmentnumber() {
        return apartmentnumber;
    }

    public void setApartmentnumber(Integer apartmentnumber) {
        this.apartmentnumber = apartmentnumber;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }

}