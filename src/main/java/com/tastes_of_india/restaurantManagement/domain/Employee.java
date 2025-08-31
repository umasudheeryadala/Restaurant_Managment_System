package com.tastes_of_india.restaurantManagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Gender;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "employee")
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name= "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "designation")
    @Enumerated(EnumType.STRING)
    private Designation designation;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "last_modified_date")
    private ZonedDateTime lastModifiedDate;

    @Column(name = "login_id")
    private String loginId;

    @Column(name= "user_name")
    private String userName;

    @OneToMany(mappedBy = "employee")
    @JsonIgnoreProperties(value = {"createdBy","employees","tables"}, allowSetters = true)
    private List<RestaurantEmployee> restaurantEmployees;

    @OneToMany(mappedBy = "createdBy",fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"employee"},allowSetters = true)
    private Set<Tables> tables;

    @OneToMany(mappedBy = "createdBy",fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"createdBy","employees","tables","menuCategories"},allowSetters = true)
    private Set<Restaurant> restaurants;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Tables> getTables() {
        return tables;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Designation getDesignation() {
        return designation;
    }

    public void setDesignation(Designation designation) {
        this.designation = designation;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public ZonedDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public void setTables(Set<Tables> tables) {
        this.tables = tables;
    }

    public Set<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(Set<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public List<RestaurantEmployee> getRestaurantEmployees() {
        return restaurantEmployees;
    }

    public void setRestaurantEmployees(List<RestaurantEmployee> restaurantEmployees) {
        this.restaurantEmployees = restaurantEmployees;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", gender=" + gender +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", designation=" + designation +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", login_id='" + loginId + '\'' +
                '}';
    }
}
