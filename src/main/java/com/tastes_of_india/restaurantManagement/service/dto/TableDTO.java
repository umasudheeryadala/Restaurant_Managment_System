package com.tastes_of_india.restaurantManagement.service.dto;

import com.tastes_of_india.restaurantManagement.domain.enumeration.TableStatus;

import java.time.ZonedDateTime;

public class TableDTO {

    private Long id;

    private String name;

    private TableStatus status;

    private boolean deleted;

    private Integer capacity;

    private ZonedDateTime createdDate;

    private RestaurantDTO restaurant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public RestaurantDTO getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantDTO restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public String toString() {
        return "TableBasicDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", deleted=" + deleted +
                ", capacity=" + capacity +
                ", createdDate=" + createdDate +
                '}';
    }
}
