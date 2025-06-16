package com.tastes_of_india.restaurantManagement.service.dto;

import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;

public class RestaurantEmployeeDTO {

    private Long employeeId;

    private String firstName;

    private String lastName;

    private String email;

    private Designation designation;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Designation getDesignation() {
        return designation;
    }

    public void setDesignation(Designation designation) {
        this.designation = designation;
    }

    @Override
    public String toString() {
        return "RestaurantEmployeeDTO{" +
                "id='" + employeeId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", designation='" + designation + '\'' +
                '}';
    }
}
