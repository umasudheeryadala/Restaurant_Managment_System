package com.tastes_of_india.restaurantManagement.domain;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class RestaurantEmployeeId {

    private Long restaurantId;

    private Long employeeId;

    public RestaurantEmployeeId(){};

    public RestaurantEmployeeId(Long restaurantId,Long employeeId){
        this.restaurantId=restaurantId;
        this.employeeId=employeeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId,employeeId);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj )  return true;
        if(!(obj instanceof RestaurantEmployeeId)) return false;
        RestaurantEmployeeId that=(RestaurantEmployeeId) obj;
        return Objects.equals(restaurantId,that.restaurantId) && Objects.equals(employeeId,that.employeeId);
    }
}
