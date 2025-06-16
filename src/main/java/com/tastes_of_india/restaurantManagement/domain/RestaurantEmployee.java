package com.tastes_of_india.restaurantManagement.domain;

import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_employee")
public class RestaurantEmployee {


    @EmbeddedId
    private RestaurantEmployeeId id=new RestaurantEmployeeId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("restaurantId")
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "designation")
    @Enumerated(EnumType.STRING)
    private Designation designation;

    public RestaurantEmployeeId getId() {
        return id;
    }

    public void setId(RestaurantEmployeeId id) {
        this.id = id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Designation getDesignation() {
        return designation;
    }

    public void setDesignation(Designation designation) {
        this.designation = designation;
    }

    @Override
    public String toString() {
        return "RestaurantEmployee{" +
                "id=" + id +
                ", restaurant=" + restaurant +
                ", employee=" + employee +
                ", designation=" + designation +
                '}';
    }
}
