package com.globallogic.bci.entity;

import javax.persistence.*;

/**
 * Phone entity representing a phone number associated with a user.
 * Stores phone number details including number, city code, and country code.
 */
@Entity
@Table(name = "phones")
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long number;

    @Column(nullable = false)
    private Integer citycode;

    @Column(nullable = false, length = 5)
    private String contrycode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Phone() {
    }

    public Phone(Long number, Integer citycode, String contrycode) {
        this.number = number;
        this.citycode = citycode;
        this.contrycode = contrycode;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Integer getCitycode() {
        return citycode;
    }

    public void setCitycode(Integer citycode) {
        this.citycode = citycode;
    }

    public String getContrycode() {
        return contrycode;
    }

    public void setContrycode(String contrycode) {
        this.contrycode = contrycode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
